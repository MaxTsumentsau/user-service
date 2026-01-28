package integration;

import annotation.ServiceFactory;
import dao.UserDao;
import dao.UserDaoImpl;
import dto.Result;
import dto.Status;
import entity.User;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;
import service.UserServiceImpl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest extends IntegrationTestBase {

     UserDao dao;
     UserService service;
     ServiceFactory serviceFactory;

     @BeforeEach
     void setUp() {
          serviceFactory = new ServiceFactory(sessionFactory);
          dao = new UserDaoImpl();
          UserServiceImpl impl = new UserServiceImpl(dao);
          service = serviceFactory.create(UserService.class, impl);
     }


     @Test
     void createUser() {
          Optional<User> beforeCreate = service.getAllUsers()
                  .stream()
                  .filter(x -> Objects.equals(x.getName(), "Golovach Lena"))
                  .findAny();
          Result<User> result = service.createUser("Golovach Lena", "LenaGolovach@gmail.com", 20);
          Result<User> afterCreate = service.getUser(result.data().getId());

          assertTrue(beforeCreate.isEmpty());
          assertEquals(Status.SUCCESS, result.status());
          assertEquals("Golovach Lena", afterCreate.data().getName());
     }

     @Test
     void createUser_constraintViolation() {
          Result<User> beforeCreate = service.getUser(1);
          assertThrows(ConstraintViolationException.class, () ->
                  service.createUser("Cackash Kavrott", beforeCreate.data().getEmail(), 23));
     }

     @Test
     void getUser_returnsSuccess() {
          Result<User> result = service.getUser(9);
          assertEquals(Status.SUCCESS, result.status());
          assertEquals("Duncan Macleod", result.data().getName());
          assertEquals("highlander@gmail.com", result.data().getEmail());
          assertEquals(534, result.data().getAge());
     }

     @Test
     void getUser_returnsNotFound() {
          Result<User> result = service.getUser(333);
          assertEquals(Status.NOT_FOUND, result.status());
     }

     @Test
     void getAllUsers() {
          List<User> result = service.getAllUsers();
          assertEquals(10, result.size());
     }

     @Test
     void updateUser() {
          User beforeUpdate = service.getUser(10).data();
          Result<User> update = service.updateUser(10, "Eminem", "superman@gmail.com", 45);
          User afterUpdate = service.getUser(10).data();

          assertNotEquals(beforeUpdate.getName(), afterUpdate.getName());
          assertEquals(beforeUpdate.getEmail(), afterUpdate.getEmail());
          assertNotEquals(beforeUpdate.getAge(), afterUpdate.getAge());
          assertEquals(Status.SUCCESS, update.status());
     }

     @Test
     void updateUser_returnsNotFound() {
          Result<User> beforeUpdate = service.getUser(17);
          Result<User> update = service.updateUser(17, "Eminem", "superman@gmail.com", 45);
          Result<User> afterUpdate = service.getUser(17);

          assertEquals(Status.NOT_FOUND, afterUpdate.status());
          assertNull(beforeUpdate.data());
          assertEquals(Status.NOT_FOUND, update.status());
     }

     @Test
     void deleteUser() {
          User beforeDelete = service.getUser(10).data();
          Result<Void> result = service.deleteUser(10);
          Result<User> afterDelete = service.getUser(10);

          assertInstanceOf(User.class, beforeDelete);
          assertEquals(Status.SUCCESS, result.status());
          assertEquals(Status.NOT_FOUND, afterDelete.status());
     }

     @Test
     void deleteUser_returnsNotFound() {
          Result<User> beforeDelete = service.getUser(12);
          Result<Void> result = service.deleteUser(12);
          Result<User> afterDelete = service.getUser(12);

          assertEquals(Status.NOT_FOUND, beforeDelete.status());
          assertEquals(Status.NOT_FOUND, result.status());
          assertEquals(Status.NOT_FOUND, afterDelete.status());
     }
}