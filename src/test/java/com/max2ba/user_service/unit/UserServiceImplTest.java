package com.max2ba.user_service.unit;

import com.max2ba.user_service.dto.UserRequest;
import com.max2ba.user_service.dto.SendEmailRequest;
import com.max2ba.user_service.dto.UserOperation;
import com.max2ba.user_service.entity.User;
import com.max2ba.user_service.exception.NotFoundException;
import com.max2ba.user_service.repository.UserRepository;
import com.max2ba.user_service.service.UserServiceImpl;
import com.max2ba.user_service.util.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
     @Mock
     UserRepository userRepository;
     @Mock
     UserMapper userMapper;
     @Mock
     ApplicationEventPublisher eventPublisher;

     @InjectMocks
     UserServiceImpl service;

     @Test
     void createUser() {
          SendEmailRequest sendEmailRequests = new SendEmailRequest(UserOperation.CREATE, "max@gmail.com");
          when(userMapper.fromCreateRequest(any(UserRequest.class)))
                  .thenReturn(new User(null, "Max", "max@gmail.com", 34, null));

          when(userRepository.save(any(User.class)))
                  .thenAnswer(invocation -> invocation.getArgument(0));
          doNothing().when(eventPublisher).publishEvent(sendEmailRequests);

          var result = service.createUser(new UserRequest("Max", "max@gmail.com", 34));

          verify(userMapper).fromCreateRequest(any());
          verify(userRepository).save(any(User.class));
          verify(eventPublisher).publishEvent(any(SendEmailRequest.class));

          assertEquals("Max", result.getName());
          assertEquals("max@gmail.com", result.getEmail());
     }

     @Test
     void getUser_returnsSuccess() {
          when(userRepository.findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a")))
                  .thenReturn(Optional.of(new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                          "Max", "max@mail.com", 34,
                          LocalDateTime.of(2026, 1, 28, 16, 13, 0))));

          var result = service.getUser(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));

          verify(userRepository).findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));
          assertEquals("Max", result.getName());
          assertEquals("max@mail.com", result.getEmail());
          assertEquals(34, result.getAge());
          assertEquals(LocalDateTime.of(2026, 1, 28, 16, 13, 0),
                  result.getCreatedAt());
     }

     @Test
     void getUser_returnsNotFound() {
          UUID id = UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a");
          when(userRepository.findById(id)).thenReturn(Optional.empty());

          assertThrows(NotFoundException.class, () -> service.getUser(id));

          verify(userRepository).findById(id);
          verifyNoMoreInteractions(userRepository);
     }

     @Test
     void getAllUsers() {
          List<User> users = List.of(
                  new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                          "Max", "max@mail.com", 34,
                          LocalDateTime.of(2026, 1, 28, 16, 13, 0)),
                  new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393b"),
                          "2ba", "2ba@mail.com", 34,
                          LocalDateTime.of(2026, 1, 28, 17, 13, 0))
          );
          Pageable pageable = PageRequest.of(0, 10);
          when(userRepository.findAll(pageable))
                  .thenReturn(new PageImpl<>(users, pageable, users.size()));

          var result = service.searchUsers(null, pageable);

          assertEquals(2, result.getTotalElements());
          assertEquals("max@mail.com", result.getContent().get(0).getEmail());
          assertEquals("2ba", result.getContent().get(1).getName());
     }

     @Test
     void getAllUsers_returnsEmptyList() {
          Pageable pageable = PageRequest.of(0, 10);
          when(userRepository.findAll(pageable)).thenReturn(Page.empty());

          var resultList = service.searchUsers(null, PageRequest.of(0, 10));

          verify(userRepository).findAll(pageable);
          assertEquals(0, resultList.getTotalElements());
     }

     @Test
     void updateUser() {
          User user = new User(
                  UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                  "Max", "max@mail.com", 34, LocalDateTime.now()
          );
          UUID id = UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a");
          UserRequest request = new UserRequest("Max", "max@mail.com", 34);

          when(userRepository.findById(id))
                  .thenReturn(Optional.of(user));

          when(userRepository.save(any(User.class)))
                  .thenReturn(user);

          var result = service.updateUser(id, request);

          verify(userRepository).findById(user.getId());
          verify(userRepository).save(user);
          assertEquals(user.getId(), result.getId());
     }

     @Test
     void updateUser_notFound() {
          when(userRepository.findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a")))
                  .thenReturn(Optional.empty());

          assertThrows(NotFoundException.class, () ->
                  service.updateUser(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                          new UserRequest("Maxim", "maxim@mail.com", 34)));
          verify(userRepository, never()).save(any());
     }

     @Test
     void deleteUser_success() {
          User user = new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                  "Max", "max@mail.com", 34, LocalDateTime.now());

          when(userRepository.findById(user.getId()))
                  .thenReturn(Optional.of(user));
          doNothing().when(userRepository).delete(user);
          doNothing().when(eventPublisher).publishEvent(any(SendEmailRequest.class));

          service.deleteUser(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));

          verify(userRepository).findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));
          verify(userRepository).delete(user);
          verify(eventPublisher).publishEvent(any(SendEmailRequest.class));
          verifyNoMoreInteractions(userRepository);
     }

     @Test
     void deleteUser_notFound() {
          UUID id = UUID.randomUUID();
          when(userRepository.findById(id)).thenReturn(Optional.empty());

          assertThrows(NotFoundException.class, () -> service.deleteUser(id));

          verify(userRepository).findById(id);
          verifyNoMoreInteractions(userRepository);
     }


     @Test
     void searchUsersByName() {
          List<User> users = List.of(new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                  "Max", "max@mail.com", 34,
                  LocalDateTime.of(2026, 1, 28, 16, 13, 0)));
          Pageable pageable = PageRequest.of(0, 10);
          when(userRepository.findByNameContainingIgnoreCase("max", pageable))
                  .thenReturn(new PageImpl<>(users, pageable, users.size()));

          var result = service.searchUsers("max", pageable);

          verify(userRepository).findByNameContainingIgnoreCase("max", pageable);
          assertEquals("Max", result.getContent().getFirst().getName());
          assertEquals("max@mail.com", result.getContent().getFirst().getEmail());
          assertEquals(34, result.getContent().getFirst().getAge());
          assertEquals(LocalDateTime.of(2026, 1, 28, 16, 13, 0),
                  result.getContent().getFirst().getCreatedAt());
          assertEquals(1, result.getTotalElements());
     }

     @Test
     void searchUsersByName_notFound() {
          Pageable pageable = PageRequest.of(0, 10);
          when(userRepository.findByNameContainingIgnoreCase("Tumensev", pageable))
                  .thenReturn(new PageImpl<>(List.of(), pageable, 0));

          var result = service.searchUsers("Tumensev", pageable);

          verify(userRepository).findByNameContainingIgnoreCase("Tumensev", pageable);
          assertEquals(0, result.getTotalElements());
     }
}