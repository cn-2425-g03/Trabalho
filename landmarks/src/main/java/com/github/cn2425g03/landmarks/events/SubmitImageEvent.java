package com.github.cn2425g03.landmarks.events;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;

public class SubmitImageEvent implements MessageReceiver {

    @Override
    public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
        System.out.println(message.getData().toStringUtf8());
        consumer.ack();
    }
}
