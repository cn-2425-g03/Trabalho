package com.github.cn2425g03.server.services;

import com.github.cn2425g03.server.observers.ReceiveImageObserver;
import com.github.cn2425g03.server.repositories.ImageInformationRepository;
import com.google.cloud.storage.Bucket;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.Topic;
import image.*;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ImageService extends ImageGrpc.ImageImplBase {

    private final static int ZOOM = 15;
    private final static String SIZE = "600x300";

    private final ImageInformationRepository imageInformationRepository;
    private final PubSubService pubSubService;
    private final CloudStorageService cloudStorageService;
    private final Bucket bucket;
    private final Topic topic;
    private final String apiKey;

    public ImageService(
            ImageInformationRepository imageInformationRepository, PubSubService pubSubService,
            CloudStorageService cloudStorageService, Bucket bucket, Topic topic, String apiKey
    ) {
        this.imageInformationRepository = imageInformationRepository;
        this.pubSubService = pubSubService;
        this.cloudStorageService = cloudStorageService;
        this.bucket = bucket;
        this.topic = topic;
        this.apiKey = apiKey;
    }

    /**
     * Handles receiving image data from the client and responds with an image identifier.
     *
     * @param responseObserver the observer to send the response back to the client.
     *
     * @return a StreamObserver to handle incoming image data.
     *
     */

    @Override
    public StreamObserver<ImageContent> submitImage(StreamObserver<ImageIdentifier> responseObserver) {
        return new ReceiveImageObserver(responseObserver, cloudStorageService, pubSubService, bucket, topic);
    }

    /**
     *
     * Sends the information of an image to the client based on the provided id.
     * The information includes the map, score, name, and location.
     *
     * @param request the identifier of the image.
     * @param responseObserver the observer to send the response to the client.
     *
     */

    @Override
    public void getImageInformation(ImageIdentifier request, StreamObserver<ImageInformation> responseObserver) {

        try {

            var imageInformation = imageInformationRepository.getById(request.getId());

            if (imageInformation.isEmpty()) {
                responseObserver.onError(
                        new StatusRuntimeException(Status.NOT_FOUND.withDescription("Image not found"))
                );
                return;
            }

            String uri = "https://maps.googleapis.com/maps/api/staticmap?"
                    + "center=" + imageInformation.getFirst().latitude() + "," + imageInformation.getFirst().longitude()
                    + "&zoom=" + ZOOM
                    + "&size=" + SIZE
                    + "&key=" + apiKey;

            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(uri))
                    .build();

            try (HttpClient httpClient = HttpClient.newHttpClient()) {

                byte[] bytes = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray()).body();
                List<ImageResult> results = imageInformation.stream()
                        .map(information -> ImageResult.newBuilder()
                                .setName(information.description())
                                .setLocation(
                                        Location.newBuilder()
                                                .setLatitude(information.latitude())
                                                .setLongitude(information.longitude())
                                                .build()
                                )
                                .setScore(information.score())
                                .build())
                        .toList();

                responseObserver.onNext(
                        ImageInformation.newBuilder()
                                .setMap(
                                        ImageContent.newBuilder()
                                                .setData(ByteString.copyFrom(bytes))
                                                .build()
                                )
                                .addAllResults(results)
                                .build()
                );

                responseObserver.onCompleted();

            } catch (IOException e) {
                responseObserver.onError(new StatusException(Status.INTERNAL.withDescription(e.getMessage())));
            }

        } catch (ExecutionException | InterruptedException e) {
            responseObserver.onError(new StatusException(Status.INTERNAL.withDescription(e.getMessage())));
        }

    }

    @Override
    public void getAllImagesDetection(Score request, StreamObserver<MonumentDetection> responseObserver) {

        try {

            for (var information : imageInformationRepository.getAllByScoreGreaterThan(request.getValue())) {

                MonumentDetection monumentDetection = MonumentDetection.newBuilder()
                        .setImageName(information.blobName())
                        .setMonumentName(information.description())
                        .build();

                responseObserver.onNext(monumentDetection);
            }

            responseObserver.onCompleted();

        } catch (ExecutionException | InterruptedException e) {
            responseObserver.onError(new StatusException(Status.INTERNAL.withDescription(e.getMessage())));
        }

    }

}
