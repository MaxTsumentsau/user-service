package com.max2ba.user_service.controller;

import com.max2ba.user_service.advice.GlobalExceptionHandler;
import com.max2ba.user_service.dto.*;
import com.max2ba.user_service.entity.User;
import com.max2ba.user_service.exception.NotFoundException;
import com.max2ba.user_service.exception.ValidationException;
import com.max2ba.user_service.service.UserFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

     @Mock
     private UserFacade userFacade;

     @InjectMocks
     private UserController userController;

     private RestTestClient client;

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
     void getUser_returns200_andDto() throws Exception {
          UUID id = UUID.randomUUID();
          UserDto dto = new UserDto(id, "Max", "max@gmail.com", 34);
          when(userFacade.getUserWithResponse(id)).thenReturn(ApiResponse.success(dto));

          ApiResponse<UserDto> response = client.get()
                  .uri("/api/users/{id}", id)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<UserDto>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(response.data().name()).isEqualTo("Max");
          assertThat(response.data().email()).isEqualTo("max@gmail.com");
          assertThat(response.data().age()).isEqualTo(34);
          assertThat((response.code())).isEqualTo(ResponseCode.SUCCESS);
     }

     @Test
     void getUser_returnsNotFoundError() {
          UUID id = UUID.randomUUID();

          when(userFacade.getUserWithResponse(id))
                  .thenThrow(new NotFoundException("Email уже существует"));

          var response = client.get()
                  .uri("/api/users/{id}", id)
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(response.code()).isEqualTo(ResponseCode.USER_NOT_FOUND_ERROR);
          assertThat(response.message()).isEqualTo(ResponseCode.USER_NOT_FOUND_ERROR.message());
     }

     @Test
     void getUser_returns400_whenUuidInvalid() {
          var result = client.get()
                  .uri("/api/users/{id}", "invalid-uuid")
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.INCORRECT_UUID_FORMAT);
          assertThat(result.message()).isEqualTo(ResponseCode.INCORRECT_UUID_FORMAT.message());
     }

     @Test
     void createUser_returns200_andDto() {
          CreateUserRequest request = new CreateUserRequest("Max", "max@gmail.com", 34);

          User user = new User(UUID.randomUUID(), "Max", "max@gmail.com", 34, LocalDateTime.now());
          UserDto dto = new UserDto(user.getId(), "Max", "max@gmail.com", 34);
          when(userFacade.createUserWithResponse(request)).thenReturn(ApiResponse.success(dto));

          var response = client.post()
                  .uri("/api/users")
                  .body(request)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<UserDto>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(response.data().name()).isEqualTo("Max");
          assertThat(response.data().email()).isEqualTo("max@gmail.com");
     }

     @Test
     void createUser_returns400_whenNameBlank() {
          CreateUserRequest request = new CreateUserRequest("", "max@gmail.com", 34);

          var result = client.post()
                  .uri("/api/users")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.VALIDATION_ERROR);
     }

     @Test
     void createUser_returns400_whenEmailExists() {
          CreateUserRequest request = new CreateUserRequest("Max", "max@gmail.com", 34);

          when(userFacade.createUserWithResponse(request))
                  .thenThrow(new ValidationException("Email уже существует"));

          var result = client.post()
                  .uri("/api/users")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.EMAIL_VALIDATION_ERROR);
     }

     @Test
     void createUser_returns400_whenEmailInvalid() {
          CreateUserRequest request = new CreateUserRequest("Max", "invalid-email", 34);

          var result = client.post()
                  .uri("/api/users")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.VALIDATION_ERROR);
     }

     @Test
     void createUser_returns400_whenAgeNegative() {
          CreateUserRequest request = new CreateUserRequest("Max", "max@gmail.com", -5);

          var result = client.post()
                  .uri("/api/users")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.VALIDATION_ERROR);
     }

     @Test
     void updateUser_returns200_andDto() {
          UUID id = UUID.randomUUID();
          UpdateUserRequest request = new UpdateUserRequest("Max", "max@gmail.com", 34);
          UserDto dto = new UserDto(id, "Max", "max@gmail.com", 34);

          when(userFacade.updateUserWithResponse(id, request))
                  .thenReturn(new ApiResponse<>(ResponseCode.SUCCESS, ResponseCode.SUCCESS.message(), dto));

          var response = client.put()
                  .uri("/api/users/{id}", id)
                  .body(request)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<UserDto>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(response.data().name()).isEqualTo("Max");
          assertThat(response.data().email()).isEqualTo("max@gmail.com");
     }

     @Test
     void updateUser_returns404_whenNotFound() {
          UUID id = UUID.randomUUID();
          UpdateUserRequest request = new UpdateUserRequest("Max", "max@gmail.com", 34);

          when(userFacade.updateUserWithResponse(id, request))
                  .thenThrow(new NotFoundException("Email уже существует"));

          var result = client.put()
                  .uri("/api/users/{id}", id)
                  .body(request)
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.USER_NOT_FOUND_ERROR);
     }

     @Test
     void updateUser_returns400_whenUuidInvalid() {
          UpdateUserRequest request = new UpdateUserRequest("Max", "max@gmail.com", 34);

          var result = client.put()
                  .uri("/api/users/{id}", "invalid-uuid")
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();
          assertThat(result.code()).isEqualTo(ResponseCode.INCORRECT_UUID_FORMAT);
     }

     @Test
     void updateUser_returns400_whenNameBlank() {
          UpdateUserRequest request = new UpdateUserRequest("", "max@gmail.com", 34);

          var result = client.put()
                  .uri("/api/users/{id}", UUID.randomUUID())
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.VALIDATION_ERROR);
     }

     @Test
     void updateUser_returns400_whenEmailInvalid() {
          UpdateUserRequest request = new UpdateUserRequest("Max", "invalid-email", 34);

          var result = client.put()
                  .uri("/api/users/{id}", UUID.randomUUID())
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.VALIDATION_ERROR);
     }

     @Test
     void updateUser_returns400_whenAgeNegative() {
          UpdateUserRequest request = new UpdateUserRequest("Max", "max@gmail.com", -5);

          var result = client.put()
                  .uri("/api/users/{id}", UUID.randomUUID())
                  .body(request)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.VALIDATION_ERROR);
     }

     @Test
     void deleteUser_returns204_whenSuccess() {
          UUID id = UUID.randomUUID();
          UserDto dto = new UserDto(id, "Max", "max@gmail.com", 34);

          when(userFacade.deleteUserWithResponse(id))
                  .thenReturn(new ApiResponse<>(ResponseCode.SUCCESS, ResponseCode.SUCCESS.message(), dto));

          var result = client.delete()
                  .uri("/api/users/{id}", id)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<UserDto>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.data().name()).isEqualTo("Max");
          assertThat(result.data().age()).isEqualTo(34);
          assertThat(result.data().email()).isEqualTo("max@gmail.com");
     }

     @Test
     void deleteUser_returns404_whenNotFound() {
          UUID id = UUID.randomUUID();

          doThrow(new NotFoundException("Email уже существует")).when(userFacade).deleteUserWithResponse(id);

          var result = client.delete()
                  .uri("/api/users/{id}", id)
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.USER_NOT_FOUND_ERROR);
     }

     @Test
     void deleteUser_returns500_whenUuidInvalid() {
          var result = client.delete()
                  .uri("/api/users/{id}", "invalid-uuid")
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(new ParameterizedTypeReference<ApiResponse<String>>() {
                  })
                  .returnResult()
                  .getResponseBody();

          assertThat(result.code()).isEqualTo(ResponseCode.INCORRECT_UUID_FORMAT);
     }

     @Test
     void search_returnsEmptyPage_whenNoUsers() {
          Pageable pageable = PageRequest.of(0, 5);

          Page<UserDto> emptyPage = new PageImpl<>(List.of(), pageable, 0);

          when(userFacade.searchUsersWithResponse(isNull(), any(Pageable.class)))
                  .thenReturn(new ApiResponse<>(
                          ResponseCode.SUCCESS,
                          ResponseCode.SUCCESS.message(),
                          emptyPage
                  ));

          client.get()
                  .uri("/api/users/search?page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.data.content").isEmpty()
                  .jsonPath("$.data.totalElements").isEqualTo(0)
                  .jsonPath("$.data.size").isEqualTo(5)
                  .jsonPath("$.data.number").isEqualTo(0);
     }


     @Test
     void search_returns400_whenPageNegative() {
          client.get()
                  .uri("/api/users/search?page=-1&size=5")
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.code").isEqualTo("ILLEGAL_ARGUMENT_ERROR");
     }

     @Test
     void search_returns400_whenSizeInvalid() {
          client.get()
                  .uri("/api/users/search?page=0&size=0")
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody()
                  .jsonPath("$.code").isEqualTo("ILLEGAL_ARGUMENT_ERROR");
     }

     @Test
     void search_returnsPage_whenNameIsNull() {
          Pageable pageable = PageRequest.of(0, 5);
          UserDto dto1 = new UserDto(UUID.randomUUID(), "Max", "max@gmail.com", 34);
          UserDto dto2 = new UserDto(UUID.randomUUID(), "Tumensev", "2ba@gmail.com", 34);
          Page<UserDto> page = new PageImpl<>(List.of(dto1, dto2), pageable, 2);

          when(userFacade.searchUsersWithResponse(null, pageable)).thenReturn(new ApiResponse<>(
                  ResponseCode.SUCCESS,
                  ResponseCode.SUCCESS.message(),
                  page));


          client.get()
                  .uri("/api/users/search?page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.data.content[0].name").isEqualTo("Max")
                  .jsonPath("$.data.content[0].email").isEqualTo("max@gmail.com")
                  .jsonPath("$.data.content[1].name").isEqualTo("Tumensev")
                  .jsonPath("$.data.content[1].email").isEqualTo("2ba@gmail.com")
                  .jsonPath("$.data.totalElements").isEqualTo(2)
                  .jsonPath("$.data.size").isEqualTo(5)
                  .jsonPath("$.data.number").isEqualTo(0)
                  .returnResult();
     }

     @Test
     void search_returnsPage_whenNameIsBlank() {
          Pageable pageable = PageRequest.of(0, 5);
          UserDto dto1 = new UserDto(UUID.randomUUID(), "Max", "max@gmail.com", 34);
          UserDto dto2 = new UserDto(UUID.randomUUID(), "Tumensev", "2ba@gmail.com", 34);

          Page<UserDto> page = new PageImpl<>(List.of(dto1, dto2), pageable, 2);

          when(userFacade.searchUsersWithResponse("", pageable)).thenReturn(new ApiResponse<>(
                  ResponseCode.SUCCESS,
                  ResponseCode.SUCCESS.message(),
                  page));


          client.get()
                  .uri("/api/users/search?name=&page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.data.content[0].name").isEqualTo("Max")
                  .jsonPath("$.data.content[0].email").isEqualTo("max@gmail.com")
                  .jsonPath("$.data.content[1].name").isEqualTo("Tumensev")
                  .jsonPath("$.data.content[1].email").isEqualTo("2ba@gmail.com")
                  .jsonPath("$.data.totalElements").isEqualTo(2)
                  .jsonPath("$.data.size").isEqualTo(5)
                  .jsonPath("$.data.number").isEqualTo(0)
                  .returnResult();
     }

     @Test
     void search_returnsFilteredPage_whenNameProvided() {
          Pageable pageable = PageRequest.of(0, 5);
          UserDto dto = new UserDto(UUID.randomUUID(), "Maxim", "max@gmail.com", 34);
          Page<UserDto> page = new PageImpl<>(List.of(dto), pageable, 1);

          when(userFacade.searchUsersWithResponse("max", pageable)).thenReturn(new ApiResponse<>(
                  ResponseCode.SUCCESS,
                  ResponseCode.SUCCESS.message(),
                  page));

          client.get()
                  .uri("/api/users/search?name=max&page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.data.content[0].name").isEqualTo("Maxim")
                  .jsonPath("$.data.totalElements").isEqualTo(1)
                  .returnResult();
     }

     @Test
     void search_returnsEmptyPage_whenNoMatch() {
          Pageable pageable = PageRequest.of(0, 5);
          Page<UserDto> emptyPage = new PageImpl<>(List.of(), pageable, 0);

          when(userFacade.searchUsersWithResponse("iamTired", pageable))
                  .thenReturn(new ApiResponse<>(
                          ResponseCode.SUCCESS,
                          ResponseCode.SUCCESS.message(),
                          emptyPage));

          client.get()
                  .uri("/api/users/search?name=iamTired&page=0&size=5")
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.data.content").isEmpty()
                  .jsonPath("$.data.totalElements").isEqualTo(0)
                  .returnResult();
     }
}


