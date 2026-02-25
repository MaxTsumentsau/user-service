package com.max2ba.user_service.integration.service;

import com.max2ba.user_service.dto.UserRequest;
import com.max2ba.user_service.entity.User;
import com.max2ba.user_service.exception.NotFoundException;
import com.max2ba.user_service.integration.IntegrationTestBase;
import com.max2ba.user_service.repository.UserRepository;
import com.max2ba.user_service.service.UserServiceImpl;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserServiceImplTest extends IntegrationTestBase {
     @Autowired
     UserServiceImpl service;
     @Autowired
     EntityManager entityManager;
     @Autowired
     UserRepository userRepository;


     @Test
     void createUser_returnsSuccess() {
          UserRequest request =
                  new UserRequest("Golovach Lena", "LenaGolovach@gmail.com", 20);

          User created = service.createUser(request);

          assertThat(created.getId()).isNotNull();

          User fromDb = service.getUser(created.getId());

          assertThat(fromDb.getName()).isEqualTo("Golovach Lena");
          assertThat(fromDb.getEmail()).isEqualTo("LenaGolovach@gmail.com");
          assertThat(fromDb.getAge()).isEqualTo(20);
     }

     @Test
     void createUser_duplicateEmail_throwsConstraintViolationException() {
          service.createUser(new UserRequest("Max", "max@mail.com", 30));

          assertThrows(ConstraintViolationException.class, () -> {
                       service.createUser(new UserRequest("Joe Parott", "max@mail.com", 23));
                       entityManager.flush();
                  }
          );
     }

     @Test
     void getUser_success() {
          User saved = userRepository.save(
                  new User(null, "Head Ake", "headake@gmail.com", 34, LocalDateTime.now())
          );

          User result = service.getUser(saved.getId());

          assertThat(result.getId()).isEqualTo(saved.getId());
          assertThat(result.getName()).isEqualTo("Head Ake");
          assertThat(result.getEmail()).isEqualTo("headake@gmail.com");
          assertThat(result.getAge()).isEqualTo(34);
     }


     @Test
     void getUser_returnsNotFound() {
          assertThrows(NotFoundException.class, () ->
                  service.getUser(UUID.randomUUID()));
     }

     @Test
     void getAllUsers() {
          var result = service.searchUsers(null, PageRequest.of(0, 20));
          assertEquals(10, result.getTotalElements());
     }

     @Test
     void updateUser_success() {
          User saved = userRepository.save(
                  new User(null, "Head Ake", "headake@gmail.com", 34, LocalDateTime.now()));
          UserRequest req = new UserRequest("CazzoCulo", "headake777@gmail.com", 45);

          User updated = service.updateUser(saved.getId(), req);

          assertThat(updated.getId()).isEqualTo(saved.getId());
          assertThat(updated.getName()).isEqualTo("CazzoCulo");
          assertThat(updated.getEmail()).isEqualTo("headake777@gmail.com");
          assertThat(updated.getAge()).isEqualTo(45);

          User fromDb = userRepository.findById(saved.getId()).orElseThrow();
          assertThat(fromDb.getName()).isEqualTo("CazzoCulo");
          assertThat(fromDb.getEmail()).isEqualTo("headake777@gmail.com");
          assertThat(fromDb.getAge()).isEqualTo(45);
     }


     @Test
     void updateUser_returnsNotFound() {
          assertThrows(NotFoundException.class, () ->
                  service.updateUser(UUID.randomUUID(),
                          new UserRequest("Eminem", "superman@gmail.com", 45))
          );
     }

     @Test
     void updateUser_duplicateEmail_throwsValidationException() {
          User user1 = userRepository.save(
                  new User(null, "Masha", "user1@gmail.com", 20, LocalDateTime.now()));
          User user2 = userRepository.save(
                  new User(null, "Dasha", "user2@gmail.com", 30, LocalDateTime.now()));

          assertThrows(ConstraintViolationException.class, () -> {
                       service.updateUser(user2.getId(),
                               new UserRequest("Pasha", user1.getEmail(), 30));
                       entityManager.flush();
                  }
          );
     }

     @Test
     void deleteUser() {
          User user = userRepository.save(
                  new User(null, "Masha", "user1@gmail.com", 20, LocalDateTime.now()));

          service.deleteUser(user.getId());
          entityManager.flush();

          assertThrows(NotFoundException.class, () -> service.getUser(user.getId()));
     }

     @Test
     void deleteUser_returnsNotFound() {
          assertThrows(NotFoundException.class, () ->
                  service.deleteUser(UUID.randomUUID()));
     }

     @Test
     void searchUsersByName() {
          userRepository.save(new User(null, "Masha", "user1@gmail.com", 20, LocalDateTime.now()));

          var result = service.searchUsers("Mash", PageRequest.of(0, 10));

          assertThat(result.getContent().getFirst().getName()).isEqualTo("Masha");
          assertThat(result.getContent().getFirst().getEmail()).isEqualTo("user1@gmail.com");
          assertThat(result.getTotalElements()).isEqualTo(1);
     }

     @Test
     void searchUsersByName_notFound() {
          var result = service.searchUsers("Java programmer", PageRequest.of(0, 10));
          assertEquals(0, result.getTotalElements());
     }
}