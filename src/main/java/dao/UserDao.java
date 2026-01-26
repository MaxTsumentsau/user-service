package dao;

import entity.User;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface UserDao {
     void save(User user, Session session);

     Optional<User> findById(Long id, Session session);

     List<User> findAll(Session session);

     void update(User user, Session session);

     void delete(User user, Session session);
}
