package com.max2ba.user_service.service;

import com.max2ba.user_service.dto.ApiResponse;
import com.max2ba.user_service.dto.CreateUserRequest;
import com.max2ba.user_service.dto.UpdateUserRequest;
import com.max2ba.user_service.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserFacade {
     ApiResponse<UserDto> createUserWithResponse(CreateUserRequest request);

     ApiResponse<UserDto> updateUserWithResponse(UUID id, UpdateUserRequest request);

     ApiResponse<UserDto> getUserWithResponse(UUID id);

     ApiResponse<Page<UserDto>> searchUsersWithResponse(String name, Pageable pageable);

     ApiResponse<UserDto> deleteUserWithResponse(UUID id);
}
