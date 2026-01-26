package dao;

import entity.User;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

     @Override
     public void save(User user, Session session) {
          session.persist(user);
     }

     @Override
     public Optional<User> findById(Long id, Session session) {
          return Optional.ofNullable(session.get(User.class, id));
     }

     @Override
     public List<User> findAll(Session session) {
          return session.createQuery("from User", User.class).list();
     }

     @Override
     public void update(User user, Session session) {
          session.merge(user);
     }

     @Override
     public void delete(User user, Session session) {
          session.remove(user);
     }
}

