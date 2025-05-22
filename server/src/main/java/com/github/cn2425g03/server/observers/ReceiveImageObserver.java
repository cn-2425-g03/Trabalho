package com.github.cn2425g03.server.observers;

import com.github.cn2425g03.server.events.SubmitImageEvent;
import com.github.cn2425g03.server.services.CloudStorageService;
import com.github.cn2425g03.server.services.PubSubService;
import com.google.cloud.storage.Bucket;
import com.google.pubsub.v1.Topic;
import image.ImageContent;
import image.ImageIdentifier;
import io.grpc.stub.StreamObserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ReceiveImageObserver implements StreamObserver<ImageContent> {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final StreamObserver<ImageIdentifier> observer;
    private final CloudStorageService cloudStorageService;
    private final PubSubService pubSubService;
    private final Bucket bucket;
    private final Topic topic;

    public ReceiveImageObserver(
            StreamObserver<ImageIdentifier> observer, CloudStorageService cloudStorageService,
            PubSubService pubSubService, Bucket bucket, Topic topic
    ) {
        this.observer = observer;
        this.cloudStorageService = cloudStorageService;
        this.pubSubService = pubSubService;
        this.bucket = bucket;
        this.topic = topic;
    }

    @Override
    public void onNext(ImageContent imageContent) {

        byte[] bytes = imageContent.getData().toByteArray();

        try {
            buffer.write(bytes);
        } catch (IOException e) {
            observer.onError(e);
        }

    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    @Override
    public void onCompleted() {

        byte[] bytes = buffer.toByteArray();
        String id = UUID.randomUUID().toString();

        try {

            cloudStorageService.uploadBlobToBucket(bucket, id, bytes);
            observer.onNext(
                    ImageIdentifier.newBuilder()
                            .setId(id)
                            .build()
            );

            SubmitImageEvent event = new SubmitImageEvent(id, bucket.getName(), id);

            observer.onCompleted();
            pubSubService.publishMessage(topic, event);

        } catch (IOException | ExecutionException | InterruptedException e) {
            observer.onError(e);
        }

    }

}
