package com.max2ba.user_service.service;

import com.max2ba.user_service.dto.UserRequest;
import com.max2ba.user_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
     User createUser(UserRequest request);

     User getUser(UUID id);

     User updateUser(UUID id, UserRequest request);

     User deleteUser(UUID id);

     Page<User> searchUsers(String name, Pageable pageable);
}
