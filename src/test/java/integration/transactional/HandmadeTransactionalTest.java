package integration.transactional;

import annotation.ServiceFactory;
import dao.UserDao;
import dao.UserDaoImpl;
import dto.Result;
import dto.Status;
import entity.User;
import integration.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;
import service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HandmadeTransactionalTest extends IntegrationTestBase {

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
    void createUser_rollbackOnException() {
        User existing = service.searchUsersByName("Max").data().getFirst();

        Status before = service.searchUsersByName("Lena").status();
        assertEquals(Status.NOT_FOUND, before);

        assertThrows(Exception.class, () ->
                service.createUser("Lena", existing.getEmail(), 30)
        );

        Result<List<User>> after = service.searchUsersByName("Lena");
        assertEquals(Status.NOT_FOUND, after.status());
    }

    @Test
    void updateUser_rollbackOnException() {
        User harry = service.searchUsersByName("Harry").data().getFirst();
        User max = service.searchUsersByName("Max").data().getFirst();

        assertThrows(Exception.class, () ->
                service.updateUser(harry.getId(), "Test", max.getEmail(), 34)
        );

        User after = service.getUser(harry.getId()).data();
        assertEquals(harry.getEmail(), after.getEmail());
        assertEquals(harry.getName(), after.getName());
        assertEquals(harry.getAge(), after.getAge());
    }
}
