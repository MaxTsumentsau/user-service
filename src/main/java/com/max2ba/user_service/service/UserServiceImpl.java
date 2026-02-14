package com.max2ba.user_service.service;

import com.max2ba.user_service.annotation.Loggable;
import com.max2ba.user_service.dto.CreateUserRequest;
import com.max2ba.user_service.dto.UpdateUserRequest;
import com.max2ba.user_service.entity.User;
import com.max2ba.user_service.exception.NotFoundException;
import com.max2ba.user_service.exception.ValidationException;
import com.max2ba.user_service.repository.UserRepository;
import com.max2ba.user_service.util.UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Slf4j
@AllArgsConstructor
@Service
@Transactional(isolation = READ_COMMITTED)
public class UserServiceImpl implements UserService {

     private final UserMapper userMapper;
     private final UserRepository userRepository;

     @Loggable
     @Override
     public User createUser(CreateUserRequest request) {
          User user = userMapper.fromCreateRequest(request);

          try {
               return userRepository.save(user);
          } catch (DataIntegrityViolationException e) {
               throw new ValidationException("Email уже существует");
          }
     }

     @Loggable
     @Override
     @Transactional(readOnly = true)
     public User getUser(UUID id) {
          return userRepository.findById(id)
                  .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
     }

     @Loggable
     @Override
     @Transactional(readOnly = true)
     public Page<User> getAllUsers(Pageable pageable) {
          return userRepository.findAll(pageable);
     }

     @Loggable
     @Override
     public User updateUser(UUID id, UpdateUserRequest req) {
          User user = userRepository.findById(id)
                  .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

          userMapper.updateUserFromRequest(req, user);

          return userRepository.save(user);
     }

     @Loggable
     @Override
     public void deleteUser(UUID id) {
          User user = userRepository.findById(id)
                  .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

          userRepository.delete(user);
     }

     @Loggable
     @Override
     @Transactional(readOnly = true)
     public Page<User> searchUsersByName(String name, Pageable pageable) {
          return userRepository.findByNameContainingIgnoreCase(name, pageable);
     }
}

