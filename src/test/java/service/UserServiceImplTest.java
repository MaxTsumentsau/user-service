package service;

import dao.UserDao;
import dto.Result;
import dto.Status;
import entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
     @Mock
     UserDao dao;

     @InjectMocks
     UserServiceImpl service;

     @Test
     void createUser() {
          doNothing().when(dao).save(any(User.class));

          var result = service.createUser("Max", "max@gmail.com", 34);

          verify(dao).save(any(User.class));
          assertEquals(Status.SUCCESS, result.status());
     }

     @Test
     void createUserWithIncorrectAge() {
          Result<User> result = service.createUser("Max", "max@gmail.com", -5);

          verify(dao, never()).save(any(User.class));
          assertEquals(Status.VALIDATION_ERROR, result.status());
     }

     @Test
     void getUser_returnsSuccess() {
          when(dao.findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a")))
                  .thenReturn(Optional.of(new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                          "Max", "max@mail.com", 34,
                          LocalDateTime.of(2026, 1, 28, 16, 13, 0))));

          //UserService service = new UserServiceImpl(dao);

          Result<User> result = service.getUser(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));

          verify(dao).findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));
          assertEquals(Status.SUCCESS, result.status());
          assertEquals("Max", result.data().getName());
          assertEquals("max@mail.com", result.data().getEmail());
          assertEquals(34, result.data().getAge());
          assertEquals(LocalDateTime.of(2026, 1, 28, 16, 13, 0),
                  result.data().getCreatedAt());
     }

     @Test
     void getUser_returnsNotFound() {
          when(dao.findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a")))
                  .thenReturn(Optional.empty());

          Result<User> result = service.getUser(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));

          verify(dao).findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));
          assertEquals(Status.NOT_FOUND, result.status());
     }

     @Test
     void getAllUsers() {
          when(dao.findAll()).thenReturn(List.of(
                          new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                                  "Max", "max@mail.com", 34,
                                  LocalDateTime.of(2026, 1, 28, 16, 13, 0)),
                          new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393b"),
                                  "2ba", "2ba@mail.com", 34,
                                  LocalDateTime.of(2026, 1, 28, 17, 13, 0))
                  )
          );

          List<User> resultList = service.getAllUsers();

          assertEquals(2, resultList.size());
          assertEquals("max@mail.com", resultList.getFirst().getEmail());
          assertEquals("2ba", resultList.getLast().getName());
     }

     @Test
     void getAllUsers_returnsEmptyList() {
          when(dao.findAll()).thenReturn(Collections.emptyList());

          List<User> resultList = service.getAllUsers();

          verify(dao).findAll();
          assertEquals(0, resultList.size());
     }

     @Test
     void updateUser() {
          User user = new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                  "Max", "max@mail.com", 34, LocalDateTime.now());

          when(dao.findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a")))
                  .thenReturn(Optional.of(user));
          doNothing().when(dao).update(user);

          Result<User> result = service.updateUser(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                  "Maxim", "maxim@mail.com", 34);
          verify(dao).update(user);
          verify(dao).findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));
          assertEquals(Status.SUCCESS, result.status());
     }

     @Test
     void updateUser_notFound() {
          when(dao.findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a")))
                  .thenReturn(Optional.empty());

          Result<User> result = service.updateUser(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                  "Maxim", "maxim@mail.com", 34);

          verify(dao, never()).update(any());
          assertEquals(Status.NOT_FOUND, result.status());
     }

     @Test
     void deleteUser() {
          User user = new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                  "Max", "max@mail.com", 34, LocalDateTime.now());

          when(dao.findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a")))
                  .thenReturn(Optional.of(user));
          doNothing().when(dao).delete(user);

          Result<Void> result = service.deleteUser(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));

          verify(dao).findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));
          verify(dao).delete(user);
          assertEquals(Status.SUCCESS, result.status());
     }

     @Test
     void deleteUser_notFound() {
          when(dao.findById(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a")))
                  .thenReturn(Optional.empty());

          Result<Void> result = service.deleteUser(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"));

          verify(dao, never()).delete(any());
          assertEquals(Status.NOT_FOUND, result.status());
     }

     @Test
     void searchUsersByName(){
          when(dao.findByNameLike("max"))
                  .thenReturn(List.of(new User(UUID.fromString("c0766553-4257-4e3c-957b-bbffdf51393a"),
                          "Max", "max@mail.com", 34,
                          LocalDateTime.of(2026, 1, 28, 16, 13, 0))));

          //UserService service = new UserServiceImpl(dao);

          Result<List<User>> result = service.searchUsersByName("max");

          verify(dao).findByNameLike("max");
          assertEquals(Status.SUCCESS, result.status());
          assertEquals("Max", result.data().getFirst().getName());
          assertEquals("max@mail.com", result.data().getFirst().getEmail());
          assertEquals(34, result.data().getFirst().getAge());
          assertEquals(LocalDateTime.of(2026, 1, 28, 16, 13, 0),
                  result.data().getFirst().getCreatedAt());
          assertEquals(1, result.data().size());
     }

     @Test
     void searchUsersByName_notFound(){
          when(dao.findByNameLike("Tumensev"))
                  .thenReturn(List.of());

          Result<List<User>> result = service.searchUsersByName("Tumensev");

          verify(dao).findByNameLike("Tumensev");
          assertEquals(Status.NOT_FOUND, result.status());
     }
}