package com.max2ba.user_service.controller;

import com.max2ba.user_service.annotation.Loggable;
import com.max2ba.user_service.dto.CreateUserRequest;
import com.max2ba.user_service.dto.UpdateUserRequest;
import com.max2ba.user_service.dto.UserDto;
import com.max2ba.user_service.service.UserService;
import com.max2ba.user_service.util.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Loggable
public class UserController {

     private final UserService service;
     private final UserMapper mapper;

     @PostMapping
     public UserDto create(@Valid @RequestBody CreateUserRequest request) {
          return mapper.toDto(service.createUser(request));
     }

     @PutMapping("/{id}")
     public UserDto update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
          return mapper.toDto(service.updateUser(id, request));
     }

     @GetMapping("/{id}")
     public UserDto get(@PathVariable UUID id) {
          return mapper.toDto(service.getUser(id));
     }

     @GetMapping
     public Page<UserDto> search(
             @RequestParam(required = false) String name,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "5") int size
     ) {
          Pageable pageable = PageRequest.of(page, size);

          if (name == null || name.isBlank()) {
               return service.getAllUsers(pageable).map(mapper::toDto);
          }

          return service.searchUsersByName(name, pageable).map(mapper::toDto);
     }

     @GetMapping("/page")
     public Page<UserDto> search(
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "5") int size
     ) {
          Pageable pageable = PageRequest.of(page, size);

          return service.getAllUsers(pageable).map(mapper::toDto);
     }

     @DeleteMapping("/{id}")
     @ResponseStatus(HttpStatus.NO_CONTENT)
     public void delete(@PathVariable UUID id) {
          service.deleteUser(id);
     }
}


