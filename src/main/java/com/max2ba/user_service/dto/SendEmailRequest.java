package com.max2ba.user_service.dto;

public record SendEmailRequest(UserOperation userOperation, String email) {
}
