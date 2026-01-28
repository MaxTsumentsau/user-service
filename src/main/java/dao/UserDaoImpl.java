package dao;

import config.SessionContext;
import entity.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

@Slf4j
public class UserDaoImpl implements UserDao {

     @Override
     public void save(User user) {
          Session session = SessionContext.get();
          session.persist(user);
     }

     @Override
     public Optional<User> findById(Integer id) {
          log.info("Called UserDaoImpl.findById with parameter {}", id);
          Session session = SessionContext.get();
          return Optional.ofNullable(session.get(User.class, id));
     }

     @Override
     public List<User> findAll() {
          log.info("Called UserDaoImpl.findAll");
          Session session = SessionContext.get();
          return session.createQuery("from User", User.class).list();
     }

     @Override
     public void update(User user) {
          log.info("Called UserDaoImpl.update with parameter {}", user);
          Session session = SessionContext.get();
          session.merge(user);
     }

     @Override
     public void delete(User user) {
          log.info("Called UserDaoImpl.delete with parameter {}", user);
          Session session = SessionContext.get();
          session.remove(user);
     }
}

