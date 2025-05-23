package com.github.cn2425g03.landmarks.repositories;

import com.github.cn2425g03.landmarks.models.ImageInformation;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;

public class ImageInformationRepository {

    private final Firestore database;

    public ImageInformationRepository(Firestore database) {
        this.database = database;
    }

    public void insert(ImageInformation imageInformation) {
        CollectionReference collection = database.collection("images_information");
        collection.add(imageInformation);
    }

}
