package dto;

import entity.User;

public record GetUserResult(GetUserStatus status, User user) {}
