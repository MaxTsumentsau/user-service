package service;

import annotation.HandmadeTransactional;
import annotation.Isolation;
import dao.UserDao;
import dto.*;
import entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

     private final UserDao userDao;

     @Override
     @HandmadeTransactional(isolation = Isolation.READ_COMMITTED)
     public Result<User> createUser(String name, String email, Integer age) {
          if (age < 0) {
               return Result.validationError("Возраст не может быть отрицательным");
          }
          User user = new User();
          user.setName(name);
          user.setEmail(email);
          user.setAge(age);

          userDao.save(user);

          return Result.success(user);
     }

     @Override
     @HandmadeTransactional(readOnly = true)
     public Result<User> getUser(UUID id) {
          return userDao.findById(id)
                  .map(Result::success)
                  .orElse(Result.notFound("Пользователь не найден"));
     }

     @Override
     @HandmadeTransactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
     public List<User> getAllUsers() {
          return userDao.findAll();
     }

     @Override
     @HandmadeTransactional
     public Result<User> updateUser(UUID id, String name, String email, Integer age) {
          User user = userDao.findById(id).orElse(null);
          if (user == null) {
               return Result.notFound("Пользователь не найден");
          }

          user.setName(name);
          user.setEmail(email);
          user.setAge(age);

          userDao.update(user);
          return Result.success(user);
     }

     @Override
     @HandmadeTransactional
     public Result<Void> deleteUser(UUID id) {
          User user = userDao.findById(id).orElse(null);
          if (user == null) {
               return Result.notFound("Пользователь не найден");
          } userDao.delete(user);
          return Result.success(null);
     }

     @Override
     @HandmadeTransactional(readOnly = true)
     public Result<List<User>> searchUsersByName(String name) {
          var users = userDao.findByNameLike(name);
          if (users.isEmpty()) {
               return Result.notFound("Пользователи не найдены");
          }
          return Result.success(users);
     }
}

