package com.github.cn2425g03.client.observers;

import image.MonumentDetection;
import io.grpc.stub.StreamObserver;

public class MonumentDetectionObserver implements StreamObserver<MonumentDetection> {

    /**
     *
     * The handler for monument detection responses.
     *
     * @param monumentDetection the monument detection response
     *
     */

    @Override
    public void onNext(MonumentDetection monumentDetection) {

        System.out.println();
        System.out.println("Image Identifier: " + monumentDetection.getImageName());
        System.out.println("Monument Name: " + monumentDetection.getMonumentName());
        System.out.println();

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
