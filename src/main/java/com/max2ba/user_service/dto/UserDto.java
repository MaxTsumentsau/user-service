package com.max2ba.user_service.dto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String name,
        String email,
        Integer age
) {
}

