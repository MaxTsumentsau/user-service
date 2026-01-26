package service;

import dto.CreateUserResult;
import dto.DeleteUserStatus;
import dto.GetUserResult;
import dto.UpdateUserResult;
import entity.User;

import java.util.List;

public interface UserService {
     CreateUserResult createUser(String name, String email, Integer age);

     GetUserResult getUser(Long id);

     List<User> getAllUsers();

     UpdateUserResult updateUser(Long id, String name, String email, Integer age);

     DeleteUserStatus deleteUser(Long id);
}
