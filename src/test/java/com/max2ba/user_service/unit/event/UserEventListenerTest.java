package com.max2ba.user_service.unit.event;

import com.max2ba.user_service.dto.SendEmailRequest;
import com.max2ba.user_service.dto.UserOperation;
import com.max2ba.user_service.listener.UserEventListener;
import com.max2ba.user_service.service.UserEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEventListenerTest {
     @Mock
     private UserEventProducer producer;

     @InjectMocks
     private UserEventListener listener;

     @Test
     void handle_shouldCallProducerWhenDelete() {
          SendEmailRequest event = new SendEmailRequest(UserOperation.DELETE, "max@gmail.com");

          listener.handle(event);

          verify(producer).sendUserEvent(event);
     }

     @Test
     void handle_shouldCallProducerWhenCreate() {
          SendEmailRequest event = new SendEmailRequest(UserOperation.CREATE, "max@gmail.com");

          listener.handle(event);

          verify(producer).sendUserEvent(event);
     }
}

