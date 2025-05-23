package com.github.cn2425g03.landmarks;

import com.github.cn2425g03.landmarks.events.SubmitImageEvent;
import com.github.cn2425g03.landmarks.repositories.ImageInformationRepository;
import com.github.cn2425g03.landmarks.services.PubSubService;
import com.github.cn2425g03.landmarks.services.VisionApiService;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.vision.v1.*;
import com.google.pubsub.v1.Subscription;

import java.io.IOException;

public class LandmarksApplication {

    private final static String PROJECT_ID = "cn2425-t3-g03";

    private final static String SUBSCRIPTION_ID = "landmarks_subscription";

    private final static String TOPIC_NAME = "cn2425-proj-g03";
    private final static String DATABASE_ID = "cn2425-g03-trabalho";

    public static void main(String[] args) throws IOException {

        PubSubService pubSubService = new PubSubService(PROJECT_ID);

        Subscription subscription = pubSubService.getSubscriptionById(SUBSCRIPTION_ID)
                .orElseGet(() -> pubSubService.createSubscription(TOPIC_NAME, SUBSCRIPTION_ID));

        FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                .setDatabaseId(DATABASE_ID)
                .build();

        Firestore database = firestoreOptions.getService();
        ImageInformationRepository imageInformationRepository = new ImageInformationRepository(database);
        VisionApiService visionApiService = new VisionApiService();


        pubSubService.createSubscriber(SUBSCRIPTION_ID, new SubmitImageEvent(visionApiService, imageInformationRepository));

        System.out.println("LandMarksApplication Started");

        while(true);

    }

}
