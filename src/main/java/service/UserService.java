package service;

import dto.Result;
import entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
     Result<User> createUser(String name, String email, Integer age);

     Result<User> getUser(UUID id);

     List<User> getAllUsers();

     Result<User> updateUser(UUID id, String name, String email, Integer age);

     Result<Void> deleteUser(UUID id);

     Result<List<User>> searchUsersByName(String name);
}
