package service;

import dto.Result;
import entity.User;

import java.util.List;

public interface UserService {
     Result<User> createUser(String name, String email, Integer age);

     Result<User> getUser(Integer id);

     List<User> getAllUsers();

     Result<User> updateUser(Integer id, String name, String email, Integer age);

     Result<Void> deleteUser(Integer id);
}
