package com.max2ba.user_service.service;

import com.max2ba.user_service.annotation.Loggable;
import com.max2ba.user_service.dto.*;
import com.max2ba.user_service.entity.User;
import com.max2ba.user_service.exception.NotFoundException;
import com.max2ba.user_service.exception.ValidationException;
import com.max2ba.user_service.repository.UserRepository;
import com.max2ba.user_service.util.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;


@AllArgsConstructor
@Service
@Loggable
@Transactional(isolation = READ_COMMITTED)
public class UserServiceImpl implements UserService, UserFacade {
     private static final String EMAIL_VALIDATION_ERROR = "Email уже существует";
     private static final String USER_NOT_FOUND = "Нарушение ограничений БД. ";

     private final UserMapper userMapper;
     private final UserRepository userRepository;
     private final ApplicationEventPublisher applicationEventPublisher;

     @Override
     public User createUser(UserRequest request) {
          User user = userMapper.fromCreateRequest(request);

          try {
               user = userRepository.save(user);
               applicationEventPublisher.publishEvent(new SendEmailRequest(UserOperation.CREATE, user.getEmail()));
               return user;
          } catch (DataIntegrityViolationException e) {
               throw new ValidationException(EMAIL_VALIDATION_ERROR);
          }
     }

     @Override
     public ApiResponse<UserDto> createUserWithResponse(UserRequest request) {
          User user = createUser(request);
          return ApiResponse.success(userMapper.toDto(user));
     }

     @Override
     @Transactional(readOnly = true)
     public User getUser(UUID id) {
          return userRepository.findById(id)
                  .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
     }

     @Override
     public ApiResponse<UserDto> getUserWithResponse(UUID id) {
          return ApiResponse.success(userMapper.toDto(getUser(id)));
     }

     @Override
     public Page<User> searchUsers(String name, Pageable pageable) {
          if (name == null || name.isBlank()) {
               return userRepository.findAll(pageable);
          }else {
               return userRepository.findByNameContainingIgnoreCase(name, pageable);
          }
     }

     @Override
     public ApiResponse<Page<UserDto>> searchUsersWithResponse(String name, int pageNumber, int size) {
          Pageable pageable = PageRequest.of(pageNumber, size);
          Page<User> page = searchUsers(name, pageable);
          return ApiResponse.success(page.map(userMapper::toDto));
     }

     @Override
     public User updateUser(UUID id, UserRequest req) {
          User user = userRepository.findById(id)
                  .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

          userMapper.updateUserFromRequest(req, user);

          return userRepository.save(user);
     }

     @Override
     public ApiResponse<UserDto> updateUserWithResponse(UUID id, UserRequest request) {
          return ApiResponse.success(userMapper.toDto(updateUser(id, request)));
     }

     @Override
     public User deleteUser(UUID id) {
          User user = userRepository.findById(id)
                  .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

          userRepository.delete(user);
          applicationEventPublisher.publishEvent(new SendEmailRequest(UserOperation.DELETE, user.getEmail()));
          return user;
     }

     @Override
     public ApiResponse<UserDto> deleteUserWithResponse(UUID id) {
          return ApiResponse.success(userMapper.toDto(deleteUser(id)));
     }
}
