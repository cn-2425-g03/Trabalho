package com.github.cn2425g03.client.services;

import com.github.cn2425g03.client.observers.ImageIdentifierObserver;
import com.github.cn2425g03.client.observers.ImageInformationObserver;
import com.github.cn2425g03.client.observers.MonumentDetectionObserver;
import com.google.protobuf.ByteString;
import image.*;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageService {

    private final ImageGrpc.ImageStub stub;

    public ImageService(ImageGrpc.ImageStub stub) {
        this.stub = stub;
    }

    /**
     *
     * Submits an image to the server and sets up a callback to receive the identifier when done
     *
     * @param fileName the name of the file to upload
     *
     */

    public void submitImage(String fileName) {

        ImageIdentifierObserver clientObserver = new ImageIdentifierObserver();
        Path path = Paths.get(fileName);
        StreamObserver<ImageContent> imageContentObserver = stub.submitImage(clientObserver);

        try (InputStream stream = Files.newInputStream(path)) {

            if (Files.size(path) > 1_000_000) {

                byte[] buffer = new byte[1024];
                int limit;

                while((limit = stream.read(buffer)) >= 0) {

                    imageContentObserver.onNext(
                            ImageContent.newBuilder()
                                    .setData(ByteString.copyFrom(ByteBuffer.wrap(buffer, 0, limit)))
                                    .build()
                    );

                }

                imageContentObserver.onCompleted();
                return;
            }

            imageContentObserver.onNext(
                    ImageContent.newBuilder()
                            .setData(ByteString.copyFrom(stream.readAllBytes()))
                            .build()
            );

            imageContentObserver.onCompleted();
        } catch (IOException e) {
            imageContentObserver.onError(new StatusException(Status.CANCELLED.withDescription(e.getMessage())));
        }

    }

    /**
     * Requests image information by image ID and handles the response using an observer.
     *
     * @param imageId the ID of the image to retrieve information for.
     * @param filename the file name associated with the observer for handling the response.
     *
     */

    public void getImageInformationById(String imageId, String filename) {

        StreamObserver<ImageInformation> imageInformationObserver = new ImageInformationObserver(filename);

        stub.getImageInformation(
                ImageIdentifier.newBuilder()
                        .setId(imageId)
                        .build(),
                imageInformationObserver
        );

    }

    /**
     *
     * Retrieve all the images with a score higher than the provided threshold.
     *
     * @param score the score threshold for the detection
     *
     */

    public void getAllImagesDetection(double score) {

        StreamObserver<MonumentDetection> monumentDetectionObserver = new MonumentDetectionObserver();

        stub.getAllImagesDetection(
                Score.newBuilder()
                        .setValue(score)
                        .build(),
                monumentDetectionObserver
        );

    }

}
