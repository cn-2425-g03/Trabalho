package com.github.cn2425g03.landmarks.services;

import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.*;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Handler;

public class PubSubService {

    private final String projectId;
    public PubSubService(String projectId) {
        this.projectId = projectId;
    }

    public Optional<Topic> getTopicByName(String name) throws IOException {

        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {

            TopicName topicName = TopicName.ofProjectTopicName(projectId, name);

            return Optional.of(topicAdminClient.getTopic(topicName));
        } catch (NotFoundException e) {
            return Optional.empty();
        }

    }

    public Subscription createSubscription(String topicId, String subscriptionId) {

        TopicName topicName = TopicName.of(projectId, topicId);

        SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);

        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {

            PushConfig pushConfig = PushConfig.getDefaultInstance();

            return subscriptionAdminClient.createSubscription(subscriptionName, topicName, pushConfig, 0);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Optional<Subscription> getSubscriptionById(String subscriptionId) {

        SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);

        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {

            return Optional.of(subscriptionAdminClient.getSubscription(subscriptionName));

        } catch (IOException | NotFoundException e) {
            return Optional.empty();
        }

    }

    public <T extends MessageReceiver> Subscriber createSubscriber(String subscriptionId, T handler) {

        ProjectSubscriptionName projectSubscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

        ExecutorProvider executorProvider = InstantiatingExecutorProvider.newBuilder()
                .setExecutorThreadCount(1)
                .build();

        Subscriber subscriber = Subscriber.newBuilder(projectSubscriptionName, handler)
                .setExecutorProvider(executorProvider)
                .build();

        subscriber.startAsync().awaitRunning();

        return subscriber;

    }

}
