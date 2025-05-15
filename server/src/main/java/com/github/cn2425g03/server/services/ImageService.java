package com.github.cn2425g03.server.services;

import com.github.cn2425g03.server.observers.ReceiveImageObserver;
import com.google.cloud.storage.Bucket;
import image.*;
import io.grpc.stub.StreamObserver;

public class ImageService extends ImageGrpc.ImageImplBase {

    private final CloudStorageService cloudStorageService;
    private final Bucket bucket;

    public ImageService(CloudStorageService cloudStorageService, Bucket bucket) {
        this.cloudStorageService = cloudStorageService;
        this.bucket = bucket;
    }

    @Override
    public StreamObserver<ImageContent> submitImage(StreamObserver<ImageIdentifier> responseObserver) {
        return new ReceiveImageObserver(responseObserver, cloudStorageService, bucket);
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
