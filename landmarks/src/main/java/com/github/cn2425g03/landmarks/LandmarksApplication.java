package com.github.cn2425g03.landmarks;

import com.github.cn2425g03.landmarks.events.SubmitImageEvent;
import com.github.cn2425g03.landmarks.services.PubSubService;
import com.google.pubsub.v1.Subscription;

public class LandmarksApplication {

    private final static String PROJECT_ID = "cn2425-t3-g03";

    private final static String SUBSCRIPTION_ID = "landmarks_subscription";

    private final static String TOPIC_NAME = "cn2425-proj-g03";

    public static void main(String[] args) {

        PubSubService pubSubService = new PubSubService(PROJECT_ID);

        Subscription subscription = pubSubService.getSubscriptionById(SUBSCRIPTION_ID)
                .orElseGet(() -> pubSubService.createSubscription(TOPIC_NAME, SUBSCRIPTION_ID));

        pubSubService.createSubscriber(SUBSCRIPTION_ID, new SubmitImageEvent());

        System.out.println("LandMarksApplication Started");

        while(true);

    }

}
