package com.github.cn2425g03.server.services;

import com.github.cn2425g03.server.observers.ReceiveImageObserver;
import com.google.cloud.storage.Bucket;
import com.google.pubsub.v1.Topic;
import image.*;
import io.grpc.stub.StreamObserver;

public class ImageService extends ImageGrpc.ImageImplBase {

    private final PubSubService pubSubService;
    private final CloudStorageService cloudStorageService;
    private final Bucket bucket;
    private final Topic topic;

    public ImageService(
            PubSubService pubSubService, CloudStorageService cloudStorageService, Bucket bucket, Topic topic
    ) {
        this.pubSubService = pubSubService;
        this.cloudStorageService = cloudStorageService;
        this.bucket = bucket;
        this.topic = topic;
    }

    @Override
    public StreamObserver<ImageContent> submitImage(StreamObserver<ImageIdentifier> responseObserver) {
        return new ReceiveImageObserver(responseObserver, cloudStorageService, pubSubService, bucket, topic);
    }

    @Override
    public void getImageInformation(ImageIdentifier request, StreamObserver<ImageInformation> responseObserver) {
        super.getImageInformation(request, responseObserver);
    }

    @Override
    public void getAllImagesDetection(Level request, StreamObserver<MonumentDetection> responseObserver) {
        super.getAllImagesDetection(request, responseObserver);
    }

}
