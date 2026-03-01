package com.max2ba.user_service.hateoas;

import com.max2ba.user_service.controller.UserController;
import com.max2ba.user_service.dto.ApiResponse;
import com.max2ba.user_service.dto.UserDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<ApiResponse<UserDto>,
        EntityModel<ApiResponse<UserDto>>> {
     @Override
     public EntityModel<ApiResponse<UserDto>> toModel(ApiResponse<UserDto> response) {
          UserDto dto = response.data();
          UUID id = dto.id();
          return EntityModel.of(
                  response,
                  linkTo(methodOn(UserController.class).get(id)).withSelfRel(),
                  linkTo(methodOn(UserController.class).update(id, null)).withRel("update"),
                  linkTo(methodOn(UserController.class).delete(id)).withRel("delete"),
                  linkTo(methodOn(UserController.class).search(null, 0, 5)).withRel("search")
          );
     }
}
