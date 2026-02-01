package integration;

import annotation.ServiceFactory;
import dao.UserDao;
import dao.UserDaoImpl;
import dto.Result;
import dto.Status;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;
import service.UserServiceImpl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
          User beforeCreate = service.searchUsersByName("Harry").data().getFirst();
          assertThrows(Exception.class, () ->
                  service.createUser("Cackash Kavrott", beforeCreate.getEmail(), 23));
     }

     @Test
     void getUser_returnsSuccess() {
          Result<List<User>> result = service.searchUsersByName("ncan");
          assertEquals(Status.SUCCESS, result.status());
          assertEquals("Duncan Macleod", result.data().getFirst().getName());
          assertEquals("highlander@gmail.com", result.data().getFirst().getEmail());
          assertEquals(534, result.data().getFirst().getAge());
     }

     @Test
     void getUser_returnsNotFound() {
          Result<User> result = service.getUser(UUID.fromString("a46b05bb-29ec-4dff-b430-7deb4e2a2ce1"));
          assertEquals(Status.NOT_FOUND, result.status());
     }

     @Test
     void getAllUsers() {
          List<User> result = service.getAllUsers();
          assertEquals(10, result.size());
     }

     @Test
     void updateUser() {
          User beforeUpdate = service.searchUsersByName("kent").data().getFirst();
          Result<User> update = service.updateUser(beforeUpdate.getId(),
                  "Eminem", "superman@gmail.com", 45);
          User afterUpdate = service.getUser(beforeUpdate.getId()).data();

          assertNotEquals(beforeUpdate.getName(), afterUpdate.getName());
          assertEquals(beforeUpdate.getEmail(), afterUpdate.getEmail());
          assertNotEquals(beforeUpdate.getAge(), afterUpdate.getAge());
          assertEquals(Status.SUCCESS, update.status());
     }

     @Test
     void updateUser_returnsNotFound() {
          Result<User> beforeUpdate = service.getUser(UUID.fromString("6487e179-3ed1-446c-8c44-bbb5ddc475da"));
          Result<User> update = service.updateUser(UUID.fromString("6487e179-3ed1-446c-8c44-bbb5ddc475da"),
                  "Eminem", "superman@gmail.com", 45);
          Result<User> afterUpdate = service.getUser(UUID.fromString("6487e179-3ed1-446c-8c44-bbb5ddc475da"));

          assertEquals(Status.NOT_FOUND, afterUpdate.status());
          assertNull(beforeUpdate.data());
          assertEquals(Status.NOT_FOUND, update.status());
     }

     @Test
     void deleteUser() {
          User beforeDelete = service.searchUsersByName("Potter").data().getFirst();
          Result<Void> result = service.deleteUser(beforeDelete.getId());
          Result<User> afterDelete = service.getUser(beforeDelete.getId());

          assertInstanceOf(User.class, beforeDelete);
          assertEquals(Status.SUCCESS, result.status());
          assertEquals(Status.NOT_FOUND, afterDelete.status());
     }

     @Test
     void deleteUser_returnsNotFound() {
          Result<User> beforeDelete = service.getUser(UUID.fromString("6487e666-3ed1-446c-8c44-bbb5ddc475dd"));
          Result<Void> result = service.deleteUser(UUID.fromString("6487e666-3ed1-446c-8c44-bbb5ddc475dd"));
          Result<User> afterDelete = service.getUser(UUID.fromString("6487e666-3ed1-446c-8c44-bbb5ddc475dd"));

          assertEquals(Status.NOT_FOUND, beforeDelete.status());
          assertEquals(Status.NOT_FOUND, result.status());
          assertEquals(Status.NOT_FOUND, afterDelete.status());
     }

     @Test
     void searchUsersByName() {
          List<User> result = service.searchUsersByName("Potter").data();
          assertEquals("Harry Potter", result.getFirst().getName());
          assertEquals("witcher@gmail.com", result.getFirst().getEmail());
          assertEquals(1, result.size());
     }

     @Test
     void searchUsersByName_notFound() {
          Result<List<User>> result = service.searchUsersByName("Java programmer");
          assertEquals(Status.NOT_FOUND, result.status());
     }
}