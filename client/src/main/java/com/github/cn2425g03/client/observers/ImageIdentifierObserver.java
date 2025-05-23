package com.github.cn2425g03.client.observers;

import image.ImageIdentifier;
import io.grpc.stub.StreamObserver;

public class ImageIdentifierObserver implements StreamObserver<ImageIdentifier> {

    /**
     *
     * Prints the image identifier when the submit operation completes successfully
     *
     * @param imageIdentifier the image identifier
     */

    @Override
    public void onNext(ImageIdentifier imageIdentifier) {
        System.out.println(imageIdentifier.getId());
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        System.out.println("Completed");
    }

}
