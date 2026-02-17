package com.max2ba.user_service.service;

import com.max2ba.user_service.dto.CreateUserRequest;
import com.max2ba.user_service.dto.UpdateUserRequest;
import com.max2ba.user_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
     User createUser(CreateUserRequest request);

     User getUser(UUID id);

     Page<User> getAllUsers(Pageable pageable);

     User updateUser(UUID id, UpdateUserRequest request);

     void deleteUser(UUID id);

     Page<User> searchUsersByName(String name, Pageable pageable);
}
