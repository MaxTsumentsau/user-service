package com.max2ba.user_service.service;

import com.max2ba.user_service.dto.ApiResponse;
import com.max2ba.user_service.dto.UserDto;
import com.max2ba.user_service.dto.UserRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserFacade {
     ApiResponse<UserDto> createUserWithResponse(UserRequest request);

     ApiResponse<UserDto> updateUserWithResponse(UUID id, UserRequest request);

     ApiResponse<UserDto> getUserWithResponse(UUID id);

     ApiResponse<Page<UserDto>> searchUsersWithResponse(String name, int page, int size);

     ApiResponse<UserDto> deleteUserWithResponse(UUID id);
}
