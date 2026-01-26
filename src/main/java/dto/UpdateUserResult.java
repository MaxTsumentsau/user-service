package dto;

import entity.User;

public record UpdateUserResult(UpdateUserStatus status, User user) {}
