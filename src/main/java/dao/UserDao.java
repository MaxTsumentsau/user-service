package dao;

import entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
     void save(User user);

     Optional<User> findById(Integer id);

     List<User> findAll();

     void update(User user);

     void delete(User user);
}
