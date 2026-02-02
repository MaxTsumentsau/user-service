package integration.dao;

import annotation.ServiceFactory;
import config.SessionContext;
import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import integration.IntegrationTestBase;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDaoImplTest extends IntegrationTestBase {

    Session session;
    Transaction tx;
    UserDao dao;

    @BeforeEach
    void openSession() {
        session = sessionFactory.openSession();
        SessionContext.set(session);
        tx = session.beginTransaction();
        dao = new UserDaoImpl();
    }

    @AfterEach
    void rollback() {
        tx.rollback();
        SessionContext.clear();
        session.close();
    }

    @Test
    void saveAndFindById() {
        User user = User.builder()
                .name("Max")
                .email("max@gmail.com")
                .age(34)
                .build();
        dao.save(user);

        User fromDb = dao.findById(user.getId()).orElseThrow();
        assertEquals("Max", fromDb.getName());
    }


    @Test
    void findById_notFound() {
        assertTrue(dao.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void findAll() {
        int size = dao.findAll().size();

        assertEquals(10, size);
    }

    @Test
    void saveAndUpdate() {
        User user = User.builder()
                .name("Max")
                .email("max@gmail.com")
                .age(34)
                .build();

        dao.save(user);

        User result1 = dao.findById(user.getId()).orElseThrow();
        assertEquals("Max", result1.getName());

        user.setName("2ba");
        user.setEmail("2ba@gmail.com");
        user.setAge(19);
        dao.update(user);

        User result2 = dao.findById(user.getId()).orElseThrow();
        assertEquals("2ba", result2.getName());
        assertEquals("2ba@gmail.com", result2.getEmail());
        assertEquals(19, result2.getAge());
    }

    @Test
    void saveAndDelete() {
        User user = User.builder()
                .name("Golovach Lena")
                .email("lena-golovach@gmail.com")
                .age(20)
                .build();
        dao.save(user);

        dao.delete(user);

        assertTrue(dao.findById(user.getId()).isEmpty());
    }

    @Test
    void findByNameLike() {
        User user = User.builder()
                .name("Golovach Lena")
                .email("lena-golovach@gmail.com")
                .age(20)
                .build();
        dao.save(user);

        List<User> result = dao.findByNameLike("ovach");
        assertEquals(1, result.size());
        assertEquals("Golovach Lena", result.getFirst().getName());
        assertEquals("lena-golovach@gmail.com", result.getFirst().getEmail());
    }

    @Test
    void findByNameLike_notFound() {
        assertTrue(dao.findByNameLike("zzz").isEmpty());
    }
}