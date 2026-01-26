package dto;

import entity.User;

public record CreateUserResult(CreateUserStatus status, User user) {}
