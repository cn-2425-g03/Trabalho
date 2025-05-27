package com.github.cn2425g03.server.repositories;

import com.github.cn2425g03.server.models.ImageInformation;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ImageInformationRepository {

    private final Firestore database;

    public ImageInformationRepository(Firestore database) {
        this.database = database;
    }

    /**
     *
     * Retrieves a list of ImageInformation objects from the "images_information" collection
     * where the field "id" matches the provided value.
     *
     * @param id image id
     * @return a list of all images information
     *
     */

    public List<ImageInformation> getById(String id) throws ExecutionException, InterruptedException {

        CollectionReference collection = database.collection("images_information");

        ApiFuture<QuerySnapshot> future = collection.whereEqualTo("id", id).get();
        QuerySnapshot querySnapshot = future.get();

        return querySnapshot.getDocuments().stream()
                .map(document -> document.toObject(ImageInformation.class))
                .toList();
    }

    public List<ImageInformation> getAllByScoreGreaterThan(double score) throws ExecutionException, InterruptedException {

        CollectionReference collection = database.collection("images_information");

        ApiFuture<QuerySnapshot> future = collection.whereGreaterThan("score", score).get();
        QuerySnapshot querySnapshot = future.get();

        return querySnapshot.getDocuments().stream()
                .map(document -> document.toObject(ImageInformation.class))
                .toList();
    }

}
