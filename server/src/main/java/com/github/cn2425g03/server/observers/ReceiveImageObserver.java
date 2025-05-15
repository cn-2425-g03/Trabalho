package com.github.cn2425g03.server.observers;

import com.github.cn2425g03.server.services.CloudStorageService;
import com.google.cloud.storage.Bucket;
import image.ImageContent;
import image.ImageIdentifier;
import io.grpc.stub.StreamObserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ReceiveImageObserver implements StreamObserver<ImageContent> {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final StreamObserver<ImageIdentifier> observer;
    private final CloudStorageService cloudStorageService;
    private final Bucket bucket;

    public ReceiveImageObserver(StreamObserver<ImageIdentifier> observer, CloudStorageService cloudStorageService, Bucket bucket) {
        this.observer = observer;
        this.cloudStorageService = cloudStorageService;
        this.bucket = bucket;
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
        //TODOO
    }

    @Override
    public void onCompleted() {

        byte[] bytes = buffer.toByteArray();
        String blobName = UUID.randomUUID().toString();

        try {

            cloudStorageService.uploadBlobToBucket(bucket, blobName, bytes);
            observer.onNext(
                    ImageIdentifier.newBuilder()
                            .setId(blobName)
                            .build()
            );

            observer.onCompleted();

        } catch (IOException e) {
            observer.onError(e);
        }

    }
}
