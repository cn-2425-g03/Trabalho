package com.github.cn2425g03.server.services;

import image.*;
import io.grpc.stub.StreamObserver;

public class ImageService extends ImageGrpc.ImageImplBase {

    @Override
    public StreamObserver<ImageContent> submitImage(StreamObserver<ImageIdentifier> responseObserver) {
        return super.submitImage(responseObserver);
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
