package com.github.cn2425g03.client.observers;

import image.ImageInformation;
import image.ImageResult;
import io.grpc.stub.StreamObserver;

import java.io.*;

public class ImageInformationObserver implements StreamObserver<ImageInformation> {

    private final String filename;

    public ImageInformationObserver(String filename) {
        this.filename = filename;
    }

    /**
     * Handles received image information by saving the map to a file
     * and printing the details of each image result.
     *
     * @param imageInformation the received image information.
     *
     */

    @Override
    public void onNext(ImageInformation imageInformation) {

        for (ImageResult imageResult : imageInformation.getResultsList()) {

            System.out.println();
            System.out.println("Monument: " + imageResult.getName());
            System.out.println("Latitude: " + imageResult.getLocation().getLatitude());
            System.out.println("Longitude: " + imageResult.getLocation().getLongitude());
            System.out.println("Score: " + imageResult.getScore());
            System.out.println();

        }

        try (OutputStream outputStream = new FileOutputStream(filename)) {

            byte[] bytes = imageInformation.getMap().getData().toByteArray();

            outputStream.write(bytes);
            outputStream.flush();

        }catch (IOException e) {
            System.out.println("File not found: " + filename);
        }

    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        System.out.println("Image information observer completed");
    }

}
