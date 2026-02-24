package com.max2ba.user_service.listener;

import com.max2ba.user_service.annotation.Loggable;
import com.max2ba.user_service.dto.SendEmailRequest;
import com.max2ba.user_service.service.UserEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Loggable
@Component
@RequiredArgsConstructor
public class UserEventListener {

     private final UserEventProducer producer;

     @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
     public void handle(SendEmailRequest event) {
          producer.sendUserEvent(event);
     }
}