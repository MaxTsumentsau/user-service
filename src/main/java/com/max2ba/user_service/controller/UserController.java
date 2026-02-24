package com.max2ba.user_service.controller;

import com.max2ba.user_service.annotation.Loggable;
import com.max2ba.user_service.dto.ApiResponse;
import com.max2ba.user_service.dto.CreateUserRequest;
import com.max2ba.user_service.dto.UpdateUserRequest;
import com.max2ba.user_service.dto.UserDto;
import com.max2ba.user_service.service.UserFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Loggable
public class UserController {

     private final UserFacade userFacade;

     @PostMapping
     public ApiResponse<UserDto> create(@Valid @RequestBody CreateUserRequest request) {
          return userFacade.createUserWithResponse(request);
     }

     @PutMapping("/{id}")
     public ApiResponse<UserDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
          return userFacade.updateUserWithResponse(id, request);
     }

     @GetMapping("/{id}")
     public ApiResponse<UserDto> get(@PathVariable UUID id) {
          return userFacade.getUserWithResponse(id);
     }

     @GetMapping("/search")
     public ApiResponse<Page<UserDto>> search(@RequestParam(required = false) String name,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "5") int size) {
          Pageable pageable = PageRequest.of(page, size);
          return userFacade.searchUsersWithResponse(name, pageable);
     }

     @DeleteMapping("/{id}")
     public ApiResponse<UserDto> delete(@PathVariable UUID id) {
          return userFacade.deleteUserWithResponse(id);
     }
}

