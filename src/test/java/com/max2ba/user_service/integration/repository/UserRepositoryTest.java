package com.max2ba.user_service.integration.repository;

import com.max2ba.user_service.entity.User;
import com.max2ba.user_service.integration.IntegrationTestBase;
import com.max2ba.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class UserRepositoryTest extends IntegrationTestBase {

     @Autowired
     private UserRepository userRepository;

     @Test
     void saveAndFindById() {
          User user = User.builder()
                  .name("Max")
                  .email("max@gmail.com")
                  .age(34)
                  .build();
          userRepository.save(user);

          User fromDb = userRepository.findById(user.getId()).orElseThrow();
          assertEquals("Max", fromDb.getName());
     }


     @Test
     void findById_notFound() {
          assertTrue(userRepository.findById(UUID.randomUUID()).isEmpty());
     }

     @Test
     void findAll() {
          int size = userRepository.findAll().size();

          assertEquals(10, size);
     }

     @Test
     void saveAndUpdate() {
          User user = User.builder()
                  .name("Max")
                  .email("max@gmail.com")
                  .age(34)
                  .build();

          userRepository.save(user);

          User result1 = userRepository.findById(user.getId()).orElseThrow();
          assertEquals("Max", result1.getName());

          user.setName("2ba");
          user.setEmail("2ba@gmail.com");
          user.setAge(19);
          userRepository.save(user);

          User result2 = userRepository.findById(user.getId()).orElseThrow();
          assertEquals("2ba", result2.getName());
          assertEquals("2ba@gmail.com", result2.getEmail());
          assertEquals(19, result2.getAge());
     }

     @Test
     void saveAndDelete() {
          User user = User.builder()
                  .name("Golovach Lena")
                  .email("lena-golovach@gmail.com")
                  .age(20)
                  .build();
          userRepository.save(user);

          userRepository.delete(user);

          assertTrue(userRepository.findById(user.getId()).isEmpty());
     }

     @Test
     void findByNameContainingIgnoreCase() {
          User user = User.builder()
                  .name("Golovach Lena")
                  .email("lena-golovach@gmail.com")
                  .age(20)
                  .build();
          userRepository.save(user);
          Pageable pageable = PageRequest.of(0, 10);

          var result = userRepository.findByNameContainingIgnoreCase("ovach", pageable);
          assertEquals(1, result.getTotalElements());
          assertEquals("Golovach Lena", result.getContent().getFirst().getName());
          assertEquals("lena-golovach@gmail.com", result.getContent().getFirst().getEmail());
     }

     @Test
     void findByNameLike_notFound() {
          Pageable pageable = PageRequest.of(0, 10);
          assertTrue(userRepository.findByNameContainingIgnoreCase("zzz", pageable).isEmpty());
     }
}