package com.max2ba.user_service.unit.event;

import com.max2ba.user_service.dto.UserRequest;
import com.max2ba.user_service.dto.SendEmailRequest;
import com.max2ba.user_service.dto.UserOperation;
import com.max2ba.user_service.entity.User;
import com.max2ba.user_service.exception.NotFoundException;
import com.max2ba.user_service.exception.ValidationException;
import com.max2ba.user_service.repository.UserRepository;
import com.max2ba.user_service.service.UserServiceImpl;
import com.max2ba.user_service.util.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServicePublishEventTest {
     @Mock
     private UserRepository userRepository;
     @Mock
     private ApplicationEventPublisher publisher;
     @Mock
     private UserMapper userMapper;

     @InjectMocks
     private UserServiceImpl service;

     @Test
     void createUser_shouldPublishEvent() {
          UserRequest request = new UserRequest("Max", "max@gmail.com", 34);
          User user = new User(UUID.randomUUID(), request.name(), request.email(), request.age(), LocalDateTime.now());
          when(userMapper.fromCreateRequest(request)).thenReturn(user);
          when(userRepository.save(user)).thenReturn(user);

          service.createUser(request);

          verify(publisher).publishEvent(new SendEmailRequest(UserOperation.CREATE, "max@gmail.com"));
     }

     @Test
     void createUser_shouldNotPublishEvent() {
          UserRequest request = new UserRequest("Max", "max@gmail.com", 34);
          User user = new User(null, request.name(), request.email(), request.age(), LocalDateTime.now());
          when(userMapper.fromCreateRequest(request)).thenReturn(user);
          when(userRepository.save(user)).thenThrow(ValidationException.class);

          assertThatThrownBy(() -> service.createUser(request)).isInstanceOf(ValidationException.class);

          verify(publisher, never()).publishEvent(new SendEmailRequest(UserOperation.CREATE, "max@gmail.com"));
     }

     @Test
     void deleteUser_shouldPublishEvent() {
          UUID id = UUID.randomUUID();
          User user = new User(id, "Max", "max@gmail.com", 34, LocalDateTime.now());
          when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
          doNothing().when(userRepository).delete(any(User.class));

          service.deleteUser(id);

          verify(publisher).publishEvent(new SendEmailRequest(UserOperation.DELETE, "max@gmail.com"));
          verifyNoMoreInteractions(userRepository);
     }

     @Test
     void deleteUser_shouldNotPublishEvent() {
          when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

          assertThatThrownBy(() -> service.deleteUser(UUID.randomUUID())).isInstanceOf(NotFoundException.class);

          verify(publisher, never()).publishEvent(
                  new SendEmailRequest(UserOperation.DELETE, "max@gmail.com")
          );
     }
}
