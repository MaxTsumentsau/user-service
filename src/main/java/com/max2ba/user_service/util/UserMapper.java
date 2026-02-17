package com.max2ba.user_service.util;

import com.max2ba.user_service.dto.CreateUserRequest;
import com.max2ba.user_service.dto.UpdateUserRequest;
import com.max2ba.user_service.dto.UserDto;
import com.max2ba.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

     UserDto toDto(User user);

     User fromCreateRequest(CreateUserRequest request);

     @Mapping(target = "id", ignore = true)
     @Mapping(target = "createdAt", ignore = true)
     void updateUserFromRequest(UpdateUserRequest request, @MappingTarget User user);
}

