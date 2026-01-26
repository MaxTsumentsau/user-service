import config.DataSourceFactory;
import config.HibernateUtil;
import dao.UserDaoImpl;
import dto.CreateUserResult;
import dto.GetUserResult;
import entity.User;
import io.github.cdimascio.dotenv.Dotenv;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import service.UserService;
import service.UserServiceImpl;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {
     public static void main(String[] args) {
          Dotenv env = Dotenv.load();

          DataSource dataSource = DataSourceFactory.create(env);

          Flyway flyway = Flyway.configure()
                  .dataSource(dataSource)
                  .locations("classpath:db/migration")
                  .load();
          flyway.migrate();

          Properties props = new Properties();
          try (var stream = Main.class.getClassLoader().getResourceAsStream("hibernate.properties")) {
               props.load(stream);
          } catch (Exception e) {
               throw new RuntimeException("Failed to load hibernate.properties", e);
          }

          // 4. Создаём SessionFactory
          SessionFactory sessionFactory = HibernateUtil.buildSessionFactory(props, dataSource);
          // 5. Создаём DAO и Service
          UserService userService = new UserServiceImpl(new UserDaoImpl(), sessionFactory);
          // 6. Консольный интерфейс
          Scanner scanner = new Scanner(System.in);
          while (true) {
               System.out.println("""
                       
                       *** User Management ***
                       1. Create user
                       2. Get user by ID
                       3. List all users
                       4. Update user
                       5. Delete user
                       0. Exit
                       
                       Choose option:\s""");
               int choice = Integer.parseInt(scanner.nextLine());
               switch (choice) {
                    case 1 -> {
                         System.out.print("Name: ");
                         String name = scanner.nextLine();
                         System.out.print("Email: ");
                         String email = scanner.nextLine();
                         System.out.print("Age: ");
                         int age;
                         try {
                              age = Integer.parseInt(scanner.nextLine());
                         } catch (NumberFormatException e) {
                              System.out.println("Age must be a number.");
                              return;
                         }

                         CreateUserResult result = userService.createUser(name, email, age);
                         switch (result.status()) {
                              case SUCCESS -> System.out.println("Created: " + result.user());
                              case ERROR -> System.out.println("Unable to create user. Please check your input.");
                         }

                    }
                    case 2 -> {
                         System.out.print("User ID: ");
                         long id = Long.parseLong(scanner.nextLine());
                         GetUserResult result = userService.getUser(id);
                         switch (result.status()) {
                              case FOUND -> System.out.println(result.user());
                              case NOT_FOUND -> System.out.println("User not found");
                         }
                    }
                    case 3 -> {
                         List<User> result = userService.getAllUsers();
                         if (result.isEmpty()) {
                              System.out.println("No users found");
                         } else {
                              result.forEach(System.out::println);
                         }
                    }
                    case 4 -> {
                         System.out.print("User ID: ");
                         long id = Long.parseLong(scanner.nextLine());
                         System.out.print("New name: ");
                         String name = scanner.nextLine();
                         System.out.print("New email: ");
                         String email = scanner.nextLine();
                         System.out.print("New age: ");
                         int age;
                         try {
                              age = Integer.parseInt(scanner.nextLine());
                         } catch (NumberFormatException e) {
                              System.out.println("Age must be a number.");
                              return;
                         }
                         var result = userService.updateUser(id, name, email, age);
                         switch (result.status()) {
                              case SUCCESS -> System.out.println("User updated: " + result.user());
                              case NOT_FOUND -> System.out.println("User not found");
                              case ERROR -> System.out.println("Unable to update user. Check your input.");
                         }
                    }
                    case 5 -> {
                         System.out.print("User ID: ");
                         long id = Long.parseLong(scanner.nextLine());
                         var result = userService.deleteUser(id);
                         switch (result) {
                              case SUCCESS -> System.out.println("User deleted.");
                              case NOT_FOUND -> System.out.println("User not found.");
                         }
                    }
                    case 0 -> {
                         System.out.println("Bye!");
                         sessionFactory.close();
                         return;
                    }
                    default -> System.out.println("Invalid option");
               }
          }
     }
}


