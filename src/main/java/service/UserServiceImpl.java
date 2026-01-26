package service;

import dao.UserDao;
import dto.*;
import entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

     private final UserDao userDao;
     private final SessionFactory sessionFactory;

     @Override
     public CreateUserResult createUser(String name, String email, Integer age) {
          try (var session = sessionFactory.openSession()) {
               var tx = session.beginTransaction();

               try {
                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setAge(age);

                    userDao.save(user, session);

                    tx.commit();
                    return new CreateUserResult(CreateUserStatus.SUCCESS, user);

               } catch (ConstraintViolationException e) {
                    tx.rollback();
                    String constraint = e.getConstraintName();
                    log.warn("Constraint violation: {}", constraint);
                    return new CreateUserResult(CreateUserStatus.ERROR, null);
               }
          }
     }


     @Override
     public GetUserResult getUser(Long id) {
          try (var session = sessionFactory.openSession()) {
               User user = userDao.findById(id, session).orElse(null);

               if (user == null) {
                    log.warn("User with id={} not found", id);
                    return new GetUserResult(GetUserStatus.NOT_FOUND, null);
               }

               return new GetUserResult(GetUserStatus.FOUND, user);
          }
     }

     @Override
     public List<User> getAllUsers() {
          try (var session = sessionFactory.openSession()) {
               return userDao.findAll(session);
          }
     }

     @Override
     public UpdateUserResult updateUser(Long id, String name, String email, Integer age) {
          try (var session = sessionFactory.openSession()) {
               var tx = session.beginTransaction();

               User user = userDao.findById(id, session).orElse(null);

               if (user == null) {
                    log.warn("Attempt to update non-existing user id={}", id);
                    tx.commit();
                    return new UpdateUserResult(UpdateUserStatus.NOT_FOUND, null);
               }

               user.setName(name);
               user.setEmail(email);
               user.setAge(age);

               try {
                    userDao.update(user, session);
                    tx.commit();
                    return new UpdateUserResult(UpdateUserStatus.SUCCESS, user);

               } catch (ConstraintViolationException e) {
                    tx.rollback();

                    String constraint = e.getConstraintName();
                    log.warn("Constraint violation during update: {}", constraint);
                    return new UpdateUserResult(UpdateUserStatus.ERROR, null);
               }
          }
     }


     @Override
     public DeleteUserStatus deleteUser(Long id) {
          try (var session = sessionFactory.openSession()) {
               var tx = session.beginTransaction();

               User user = session.get(User.class, id);
               if (user == null) {
                    log.warn("Attempt to delete non-existing user with id={}", id);
                    tx.commit();
                    return DeleteUserStatus.NOT_FOUND;
               }
               userDao.delete(user, session);
               tx.commit();
               return DeleteUserStatus.SUCCESS;
          }
     }
}

