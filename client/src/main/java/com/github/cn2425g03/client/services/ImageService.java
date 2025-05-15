package com.github.cn2425g03.client.services;

import com.github.cn2425g03.client.observers.ImageIdentifierObserver;
import com.google.protobuf.ByteString;
import image.ImageContent;
import image.ImageGrpc;
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
            imageContentObserver.onError(e);
        }

    }

}
