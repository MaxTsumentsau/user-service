package com.max2ba.user_service.controller;

import com.max2ba.user_service.annotation.Loggable;
import com.max2ba.user_service.dto.ApiResponse;
import com.max2ba.user_service.dto.UserDto;
import com.max2ba.user_service.dto.UserRequest;
import com.max2ba.user_service.hateoas.UserModelAssembler;
import com.max2ba.user_service.service.UserFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Loggable
public class UserController {

     private final UserFacade userFacade;
     private final UserModelAssembler assembler;

     @PostMapping
     public EntityModel<ApiResponse<UserDto>> create(@Valid @RequestBody UserRequest request) {
          return assembler.toModel(userFacade.createUserWithResponse(request));
     }

     @PutMapping("/{id}")
     public EntityModel<ApiResponse<UserDto>> update(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
          return assembler.toModel(userFacade.updateUserWithResponse(id, request));
     }

     @GetMapping("/{id}")
     public EntityModel<ApiResponse<UserDto>> get(@PathVariable UUID id) {
          return assembler.toModel(userFacade.getUserWithResponse(id));
     }

     @GetMapping("/search")
     public ApiResponse<Page<UserDto>> search(@RequestParam(required = false) String name,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "5") int size) {
          return userFacade.searchUsersWithResponse(name, page, size);
     }

     @DeleteMapping("/{id}")
     public EntityModel<ApiResponse<UserDto>> delete(@PathVariable UUID id) {
          return assembler.toModel(userFacade.deleteUserWithResponse(id));
     }
}

