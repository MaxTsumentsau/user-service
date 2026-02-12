package dao;

import entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDao {
     void save(User user);

     Optional<User> findById(UUID id);

     List<User> findAll();

     void update(User user);

     void delete(User user);

     List<User> findByNameLike(String pattern);
}
