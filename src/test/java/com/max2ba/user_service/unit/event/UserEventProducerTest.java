package com.max2ba.user_service.unit.event;

import com.max2ba.user_service.dto.SendEmailRequest;
import com.max2ba.user_service.dto.UserOperation;
import com.max2ba.user_service.service.UserEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEventProducerTest {

     @Mock
     private KafkaTemplate<String, SendEmailRequest> kafkaTemplate;

     @InjectMocks
     private UserEventProducer producer;

     @Test
     void sendUserEvent_shouldCallKafkaTemplateSend() {
          SendEmailRequest event = new SendEmailRequest(UserOperation.CREATE, "max@gmail.com");

          producer.sendUserEvent(event);

          verify(kafkaTemplate, times(1)).send("user-events", event);
     }
}

