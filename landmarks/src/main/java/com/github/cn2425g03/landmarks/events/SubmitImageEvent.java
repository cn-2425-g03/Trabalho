package com.github.cn2425g03.landmarks.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cn2425g03.landmarks.models.ImageInformation;
import com.github.cn2425g03.landmarks.repositories.ImageInformationRepository;
import com.github.cn2425g03.landmarks.services.VisionApiService;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.LocationInfo;
import com.google.pubsub.v1.PubsubMessage;

import java.util.List;
import java.util.Map;

public class SubmitImageEvent implements MessageReceiver {

    private final VisionApiService visionApiService;
    private final ImageInformationRepository imageInformationRepository;

    public SubmitImageEvent(VisionApiService visionApiService, ImageInformationRepository imageInformationRepository) {
        this.visionApiService = visionApiService;
        this.imageInformationRepository = imageInformationRepository;
    }

    /**
     *
     * This method retrieves the JSON data from the pubsubMessage, deserializes it into a SubmitImageEventMessage,
     * calls the Vision API to get the associated monuments, and saves the results in Firestore.
     *
     * @param pubsubMessage the message received from the queue
     * @param consumer the consumer to be called when the message is processed successfully
     */

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage, AckReplyConsumer consumer) {

        ObjectMapper objectMapper = new ObjectMapper();
        String json = pubsubMessage.getData().toStringUtf8();

        try {

            SubmitImageEventMessage message = objectMapper.readValue(json, SubmitImageEventMessage.class);
            ImageInformation[] imagesInformation = visionApiService.getLandmarkInformation(
                    message.bucketName, message.blobName
            ).stream().map(entityAnnotation -> {

                LocationInfo locationInfo = entityAnnotation.getLocationsList().getFirst();

                return new ImageInformation(
                        message.getId(), entityAnnotation.getDescription(), message.getBucketName(), message.getBlobName(),
                        locationInfo.getLatLng().getLatitude(), locationInfo.getLatLng().getLongitude(), entityAnnotation.getScore()
                );

            }).toArray(ImageInformation[]::new);

            imageInformationRepository.insert(imagesInformation);
            consumer.ack();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public static class SubmitImageEventMessage {

        private String id;
        private String bucketName;
        private String blobName;

        public String getId() {
            return id;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getBlobName() {
            return blobName;
        }

    }

}
