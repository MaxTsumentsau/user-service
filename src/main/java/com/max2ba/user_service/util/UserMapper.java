package com.max2ba.user_service.util;

import com.max2ba.user_service.dto.UserRequest;
import com.max2ba.user_service.dto.UserDto;
import com.max2ba.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

     UserDto toDto(User user);

     User fromCreateRequest(UserRequest request);

     @Mapping(target = "id", ignore = true)
     @Mapping(target = "createdAt", ignore = true)
     void updateUserFromRequest(UserRequest request, @MappingTarget User user);
}

