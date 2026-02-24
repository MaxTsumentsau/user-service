package com.max2ba.user_service.service;

import com.max2ba.user_service.annotation.Loggable;
import com.max2ba.user_service.dto.KafkaTopic;
import com.max2ba.user_service.dto.SendEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Loggable
@Service
@RequiredArgsConstructor
public class UserEventProducer {
     private final KafkaTemplate<String, SendEmailRequest> kafkaTemplate;

     public void sendUserEvent(SendEmailRequest message) {
          kafkaTemplate.send(KafkaTopic.USER_EVENTS.getTopicName(), message);
     }
}
