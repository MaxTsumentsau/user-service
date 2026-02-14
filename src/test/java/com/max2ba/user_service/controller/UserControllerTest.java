package com.max2ba.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max2ba.user_service.advice.GlobalExceptionHandler;
import com.max2ba.user_service.dto.CreateUserRequest;
import com.max2ba.user_service.dto.UpdateUserRequest;
import com.max2ba.user_service.dto.UserDto;
import com.max2ba.user_service.entity.User;
import com.max2ba.user_service.exception.NotFoundException;
import com.max2ba.user_service.exception.ValidationException;
import com.max2ba.user_service.service.UserService;
import com.max2ba.user_service.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

     @Mock
     private UserService userService;

     @Mock
     private UserMapper userMapper;

     @InjectMocks
     private UserController userController;

     private RestTestClient client;
     private ObjectMapper objectMapper = new ObjectMapper();

     @BeforeEach
     void setUp() {
          //MockMvc с контроллером и контроллерЭдвайсом
          MockMvc mockMvc = MockMvcBuilders
                  .standaloneSetup(userController)
                  .setControllerAdvice(new GlobalExceptionHandler())
                  .build();

          //привязка RestTestClient к MockMvc
          client = RestTestClient.bindTo(mockMvc).build();
     }


     @Test
     void getUser_returns200_andDto() {
          UUID id = UUID.randomUUID();
          User user = new User(id, "Max", "max@gmail.com", 34, LocalDateTime.now());
          UserDto dto = new UserDto(id, "Max", "max@gmail.com", 34);
          when(userService.getUser(id)).thenReturn(user);
          when(userMapper.toDto(user)).thenReturn(dto);

          UserDto response = client.get()
                  .uri("/api/users/{id}", id)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(UserDto.class)
                  .returnResult()
                  .getResponseBody();

          assertThat(response.name()).isEqualTo("Max");
          assertThat(response.email()).isEqualTo("max@gmail.com");
          assertThat(response.age()).isEqualTo(34);
     }

     @Test
     void getUser_returns404_whenNotFound() {
          UUID id = UUID.randomUUID();

          when(userService.getUser(id))
                  .thenThrow(new NotFoundException("Пользователь не найден"));

          var response = client.get()
                  .uri("/api/users/{id}", id)
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectBody()
                  .jsonPath("$.error").isEqualTo("Пользователь не найден")
                  .returnResult();
     }

     @Test
     void getUser_returns400_whenUuidInvalid() {
          client.get()
                  .uri("/api/users/{id}", "invalid-uuid")
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.error").isEqualTo("Некорректный формат UUID");
     }

     @Test
     void createUser_returns200_andDto() throws Exception {
          CreateUserRequest request = new CreateUserRequest("Max", "max@gmail.com", 34);

          User user = new User(UUID.randomUUID(), "Max", "max@gmail.com", 34, LocalDateTime.now());
          UserDto dto = new UserDto(user.getId(), "Max", "max@gmail.com", 34);

          when(userService.createUser(request)).thenReturn(user);
          when(userMapper.toDto(user)).thenReturn(dto);

          UserDto response = client.post()
                  .uri("/api/users")
                  .body(request)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(UserDto.class)
                  .returnResult()
                  .getResponseBody();

          assertThat(response.name()).isEqualTo("Max");
          assertThat(response.email()).isEqualTo("max@gmail.com");
     }

     @Test
     void createUser_returns400_whenNameBlank() {
          CreateUserRequest request = new CreateUserRequest("", "max@gmail.com", 34);

          client.post()
                  .uri("/api/users")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.name").isEqualTo("Имя не может быть пустым");
     }

     @Test
     void createUser_returns400_whenEmailExists() {
          CreateUserRequest request = new CreateUserRequest("Max", "max@gmail.com", 34);

          when(userService.createUser(request))
                  .thenThrow(new ValidationException("Email уже существует"));

          client.post()
                  .uri("/api/users")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.error").isEqualTo("Email уже существует");
     }

     @Test
     void createUser_returns400_whenEmailInvalid() {
          CreateUserRequest request = new CreateUserRequest("Max", "invalid-email", 34);

          client.post()
                  .uri("/api/users")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.email").isEqualTo("Некорректный формат email");
     }

     @Test
     void createUser_returns400_whenAgeNegative() {
          CreateUserRequest request = new CreateUserRequest("Max", "max@gmail.com", -5);

          client.post()
                  .uri("/api/users")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.age").isEqualTo("Возраст не может быть отрицательным");
     }

     @Test
     void updateUser_returns200_andDto() {
          UUID id = UUID.randomUUID();
          UpdateUserRequest request = new UpdateUserRequest("Max", "max@gmail.com", 34);
          User user = new User(id, "Max", "max@gmail.com", 34, LocalDateTime.now());
          UserDto dto = new UserDto(id, "Max", "max@gmail.com", 34);

          when(userService.updateUser(id, request)).thenReturn(user);
          when(userMapper.toDto(user)).thenReturn(dto);

          UserDto response = client.put()
                  .uri("/api/users/{id}", id)
                  .body(request)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(UserDto.class)
                  .returnResult()
                  .getResponseBody();

          assertThat(response.name()).isEqualTo("Max");
          assertThat(response.email()).isEqualTo("max@gmail.com");
     }

     @Test
     void updateUser_returns404_whenNotFound() {
          UUID id = UUID.randomUUID();
          UpdateUserRequest request = new UpdateUserRequest("Max", "max@gmail.com", 34);

          when(userService.updateUser(id, request))
                  .thenThrow(new NotFoundException("Пользователь не найден"));

          client.put()
                  .uri("/api/users/{id}", id)
                  .body(request)
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectBody()
                  .jsonPath("$.error").isEqualTo("Пользователь не найден");
     }

     @Test
     void updateUser_returns400_whenUuidInvalid() {
          UpdateUserRequest request = new UpdateUserRequest("Max", "max@gmail.com", 34);

          client.put()
                  .uri("/api/users/{id}", "invalid-uuid")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.error").isEqualTo("Некорректный формат UUID");
     }

     @Test
     void updateUser_returns400_whenNameBlank() {
          UpdateUserRequest request = new UpdateUserRequest("", "max@gmail.com", 34);

          client.put()
                  .uri("/api/users/{id}", UUID.randomUUID())
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.name").isEqualTo("Имя не может быть пустым");
     }

     @Test
     void updateUser_returns400_whenEmailInvalid() {
          UpdateUserRequest request = new UpdateUserRequest("Max", "invalid-email", 34);

          client.put()
                  .uri("/api/users/{id}", UUID.randomUUID())
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.email").isEqualTo("Некорректный формат email");
     }

     @Test
     void updateUser_returns400_whenAgeNegative() {
          UpdateUserRequest request = new UpdateUserRequest("Max", "max@gmail.com", -5);

          client.put()
                  .uri("/api/users/{id}", UUID.randomUUID())
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.age").isEqualTo("Возраст не может быть отрицательным");
     }

     @Test
     void deleteUser_returns204_whenSuccess() {
          UUID id = UUID.randomUUID();

          doNothing().when(userService).deleteUser(id);

          client.delete()
                  .uri("/api/users/{id}", id)
                  .exchange()
                  .expectStatus().isNoContent();
     }

     @Test
     void deleteUser_returns404_whenNotFound() {
          UUID id = UUID.randomUUID();

          doThrow(new NotFoundException("Пользователь не найден"))
                  .when(userService).deleteUser(id);

          client.delete()
                  .uri("/api/users/{id}", id)
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectBody()
                  .jsonPath("$.error").isEqualTo("Пользователь не найден");
     }

     @Test
     void deleteUser_returns500_whenUuidInvalid() {
          client.delete()
                  .uri("/api/users/{id}", "invalid-uuid")
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.error").isEqualTo("Некорректный формат UUID");
     }

     @Test
     void search_returns200_andPageOfUsers() {
          Pageable pageable = PageRequest.of(0, 5);
          User user1 = new User(UUID.randomUUID(), "Max", "max@gmail.com", 34, LocalDateTime.now());
          User user2 = new User(UUID.randomUUID(), "Tumensev", "2ba@gmail.com", 34,LocalDateTime.now());
          UserDto dto1 = new UserDto(user1.getId(), "Max", "max@gmail.com", 34);
          UserDto dto2 = new UserDto(user1.getId(), "Tumensev", "2ba@gmail.com", 34);

          Page<User> page = new PageImpl<>(List.of(user1, user2), pageable, 2);

          when(userService.getAllUsers(pageable)).thenReturn(page);
          when(userMapper.toDto(user1)).thenReturn(dto1);
          when(userMapper.toDto(user2)).thenReturn(dto2);

          var response = client.get()
                  .uri("/api/users/page?page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.content[0].name").isEqualTo("Max")
                  .jsonPath("$.content[0].email").isEqualTo("max@gmail.com")
                  .jsonPath("$.content[1].name").isEqualTo("Tumensev")
                  .jsonPath("$.content[1].email").isEqualTo("2ba@gmail.com")
                  .jsonPath("$.totalElements").isEqualTo(2)
                  .jsonPath("$.size").isEqualTo(5)
                  .jsonPath("$.number").isEqualTo(0)
                  .returnResult();
     }

     @Test
     void search_returnsEmptyPage_whenNoUsers() {
          Pageable pageable = PageRequest.of(0, 5);

          Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

          when(userService.getAllUsers(pageable)).thenReturn(emptyPage);

          client.get()
                  .uri("/api/users/page?page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.content").isEmpty()
                  .jsonPath("$.totalElements").isEqualTo(0)
                  .jsonPath("$.size").isEqualTo(5)
                  .jsonPath("$.number").isEqualTo(0);
     }

     @Test
     void search_returns400_whenPageNegative() {
          client.get()
                  .uri("/api/users/page?page=-1&size=5")
                  .exchange()
                  .expectStatus().isBadRequest();
     }

     @Test
     void search_returns400_whenSizeInvalid() {
          client.get()
                  .uri("/api/users/page?page=0&size=0")
                  .exchange()
                  .expectStatus().isBadRequest();
     }

     @Test
     void search_returnsPage_whenNameIsNull() {
          Pageable pageable = PageRequest.of(0, 5);

          User user1 = new User(UUID.randomUUID(), "Max", "max@gmail.com", 34, LocalDateTime.now());
          User user2 = new User(UUID.randomUUID(), "Tumensev", "2ba@gmail.com", 34,LocalDateTime.now());
          UserDto dto1 = new UserDto(user1.getId(), "Max", "max@gmail.com", 34);
          UserDto dto2 = new UserDto(user1.getId(), "Tumensev", "2ba@gmail.com", 34);

          Page<User> page = new PageImpl<>(List.of(user1, user2), pageable, 2);

          when(userService.getAllUsers(pageable)).thenReturn(page);
          when(userMapper.toDto(user1)).thenReturn(dto1);
          when(userMapper.toDto(user2)).thenReturn(dto2);

          client.get()
                  .uri("/api/users?page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.content[0].name").isEqualTo("Max")
                  .jsonPath("$.content[0].email").isEqualTo("max@gmail.com")
                  .jsonPath("$.content[1].name").isEqualTo("Tumensev")
                  .jsonPath("$.content[1].email").isEqualTo("2ba@gmail.com")
                  .jsonPath("$.totalElements").isEqualTo(2)
                  .jsonPath("$.size").isEqualTo(5)
                  .jsonPath("$.number").isEqualTo(0)
                  .returnResult();
     }

     @Test
     void search_returnsPage_whenNameIsBlank() {
          Pageable pageable = PageRequest.of(0, 5);
          User user1 = new User(UUID.randomUUID(), "Max", "max@gmail.com", 34, LocalDateTime.now());
          User user2 = new User(UUID.randomUUID(), "Tumensev", "2ba@gmail.com", 34,LocalDateTime.now());
          UserDto dto1 = new UserDto(user1.getId(), "Max", "max@gmail.com", 34);
          UserDto dto2 = new UserDto(user1.getId(), "Tumensev", "2ba@gmail.com", 34);

          Page<User> page = new PageImpl<>(List.of(user1, user2), pageable, 2);

          when(userService.getAllUsers(pageable)).thenReturn(page);
          when(userMapper.toDto(user1)).thenReturn(dto1);
          when(userMapper.toDto(user2)).thenReturn(dto2);

          client.get()
                  .uri("/api/users?name=&page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.content[0].name").isEqualTo("Max")
                  .jsonPath("$.content[0].email").isEqualTo("max@gmail.com")
                  .jsonPath("$.content[1].name").isEqualTo("Tumensev")
                  .jsonPath("$.content[1].email").isEqualTo("2ba@gmail.com")
                  .jsonPath("$.totalElements").isEqualTo(2)
                  .jsonPath("$.size").isEqualTo(5)
                  .jsonPath("$.number").isEqualTo(0)
                  .returnResult();
     }

     @Test
     void search_returnsFilteredPage_whenNameProvided() {
          Pageable pageable = PageRequest.of(0, 5);
          User user = new User(UUID.randomUUID(), "Maxim", "max@gmail.com", 34, LocalDateTime.now());
          UserDto dto = new UserDto(user.getId(), "Maxim", "max@gmail.com", 34);

          Page<User> page = new PageImpl<>(List.of(user), pageable, 1);

          when(userService.searchUsersByName("max", pageable)).thenReturn(page);
          when(userMapper.toDto(user)).thenReturn(dto);

          client.get()
                  .uri("/api/users?name=max&page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.content[0].name").isEqualTo("Maxim")
                  .jsonPath("$.totalElements").isEqualTo(1)
                  .returnResult();
     }

     @Test
     void search_returnsEmptyPage_whenNoMatch() {
          Pageable pageable = PageRequest.of(0, 5);
          Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

          when(userService.searchUsersByName("iamTired", pageable)).thenReturn(emptyPage);

          client.get()
                  .uri("/api/users?name=iamTired&page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.content").isEmpty()
                  .jsonPath("$.totalElements").isEqualTo(0)
                  .returnResult();;
     }
}


