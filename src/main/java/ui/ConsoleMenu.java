package ui;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.UserService;

import java.util.Scanner;

@Slf4j
@AllArgsConstructor
public class ConsoleMenu {

     private final UserService userService;
     private final ErrorHandler errorHandler;
     private final ConsoleRenderer renderer;
     private final InputValidator validator;

     public void start() {
          Scanner scanner = new Scanner(System.in);

          while (true) {
               printMenu();

               String choice = scanner.nextLine();

               try {
                    switch (choice) {
                         case "1" -> createUser(scanner);
                         case "2" -> listUsers();
                         case "3" -> getUser(scanner);
                         case "4" -> updateUser(scanner);
                         case "5" -> deleteUser(scanner);
                         case "0" -> {
                              System.out.println("Выход...");
                              return;
                         }
                         default -> System.out.println("Неверный выбор");
                    }
               } catch (Exception e) {
                    errorHandler.handle(e);
               }
          }
     }

     private void printMenu() {
          log.info("Called method ConsoleMenu.printMenu()/");
          System.out.print("""

    === Меню ===
    1. Создать пользователя
    2. Показать всех пользователей
    3. Найти пользователя по ID
    4. Обновить пользователя
    5. Удалить пользователя
    0. Выход
    Выберите пункт:\s""");
     }

     private void createUser(Scanner scanner) {
          System.out.print("Введите имя: ");
          String name = scanner.nextLine();

          System.out.print("Введите email: ");
          String email = scanner.nextLine();

          System.out.print("Введите возраст: ");
          Integer age = validator.readInt(scanner);

          log.info("Called ConsoleMenu.createUser() with parameters: name={}, email={}, age={}", name, email, age);
          var result = userService.createUser(name, email, age);
          renderer.render(result);
     }

     private void listUsers() {
          var result = userService.getAllUsers();
          log.info("Called ConsoleMenu.listUsers()");
          renderer.renderList(result);
     }

     private void getUser(Scanner scanner) {
          System.out.print("Введите ID: ");
          Integer id = validator.readInt(scanner);
          log.info("Called ConsoleMenu.getUser() with parameter: id={}", id);

          var result = userService.getUser(id);
          renderer.render(result);
     }

     private void updateUser(Scanner scanner) {
          System.out.print("Введите ID: ");
          Integer id = validator.readInt(scanner);

          System.out.print("Введите новое имя: ");
          String name = scanner.nextLine();

          System.out.print("Введите новый email: ");
          String email = validator.readEmail(scanner);

          System.out.print("Введите новый возраст: ");
          Integer age = validator.readInt(scanner);

          log.info("Called ConsoleMenu.updateUser() with parameters: id={}, name={}, email={}, age={}",
                  id, name, email, age);

          var result = userService.updateUser(id, name, email, age);
          renderer.render(result);
     }

     private void deleteUser(Scanner scanner) {
          System.out.print("Введите ID: ");
          Integer id = validator.readInt(scanner);

          log.info("Called ConsoleMenu.deleteUser() with parameter id={}", id);

          var result = userService.deleteUser(id);
          renderer.render(result);
     }
}

