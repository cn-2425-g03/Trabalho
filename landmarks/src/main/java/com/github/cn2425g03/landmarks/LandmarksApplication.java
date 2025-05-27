package com.github.cn2425g03.landmarks;

import com.github.cn2425g03.landmarks.events.SubmitImageEvent;
import com.github.cn2425g03.landmarks.repositories.ImageInformationRepository;
import com.github.cn2425g03.landmarks.services.PubSubService;
import com.github.cn2425g03.landmarks.services.VisionApiService;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.Subscription;

public class LandmarksApplication {

    private final static String PROJECT_ID = "cn2425-t3-g03";

    private final static String SUBSCRIPTION_ID = "landmarks_subscription";

    private final static String TOPIC_NAME = "cn2425-proj-g03";
    private final static String DATABASE_ID = "cn2425-g03-trabalho";

    public static void main(String[] args) {

        PubSubService pubSubService = new PubSubService(PROJECT_ID);

        Subscription subscription = pubSubService.getSubscriptionById(SUBSCRIPTION_ID)
                .orElseGet(() -> {
                    System.out.println("Creating new subscription: " + SUBSCRIPTION_ID);
                    return pubSubService.createSubscription(TOPIC_NAME, SUBSCRIPTION_ID);
                });

        System.out.println("Subscription " + subscription.getName() + " launched successfully");

        FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                .setDatabaseId(DATABASE_ID)
                .build();

        Firestore database = firestoreOptions.getService();
        ImageInformationRepository imageInformationRepository = new ImageInformationRepository(database);
        VisionApiService visionApiService = new VisionApiService();
        Subscriber subscriber = pubSubService.createSubscriber(SUBSCRIPTION_ID, new SubmitImageEvent(visionApiService, imageInformationRepository));

        subscriber.startAsync().awaitRunning();

        System.out.println("LandMarksApplication Started");

        subscriber.awaitTerminated();
    }

}
