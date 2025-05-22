package com.github.cn2425g03.server.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class PubSubService {

    private final String projectId;

    public PubSubService(String projectId) {
        this.projectId = projectId;
    }

    public Topic createTopic(String name) {

        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {

            TopicName topicName = TopicName.ofProjectTopicName(projectId, name);

            return topicAdminClient.createTopic(topicName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Optional<Topic> getTopicByName(String name) throws IOException {

        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {

            TopicName topicName = TopicName.ofProjectTopicName(projectId, name);

            return Optional.of(topicAdminClient.getTopic(topicName));
        } catch (NotFoundException e) {
            return Optional.empty();
        }

    }

    public void publishMessage(Topic topic, Object message) throws IOException, ExecutionException, InterruptedException {

        TopicName topicName = TopicName.parse(topic.getName());
        Publisher publisher = Publisher.newBuilder(topicName).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(message);

        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(json))
                .build();

        publisher.publish(pubsubMessage).get();
        publisher.shutdown();
    }

}
