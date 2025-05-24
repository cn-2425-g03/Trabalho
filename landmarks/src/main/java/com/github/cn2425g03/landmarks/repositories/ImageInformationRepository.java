package com.github.cn2425g03.landmarks.repositories;

import com.github.cn2425g03.landmarks.models.ImageInformation;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;

public class ImageInformationRepository {

    private final Firestore database;

    public ImageInformationRepository(Firestore database) {
        this.database = database;
    }

    /**
     * @param imageInformation an array of images information to save in firestore
     */

    public void insert(ImageInformation... imageInformation) {

        WriteBatch batch = database.batch();
        CollectionReference collection = database.collection("images_information");

        for (ImageInformation information : imageInformation)
            batch.set(collection.document(), information);

        batch.commit();
    }

}
