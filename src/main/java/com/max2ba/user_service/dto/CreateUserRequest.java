package com.max2ba.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank(message = "Имя не может быть пустым")
        String name,

        @Email(message = "Некорректный формат email")
        @NotBlank(message = "Email не может быть пустым")
        String email,

        @Min(value = 0, message = "Возраст не может быть отрицательным")
        Integer age
) {
}

