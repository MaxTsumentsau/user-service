package ui;

import dto.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.UserService;

import java.util.Scanner;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class ConsoleMenu {

     private final UserService userService;
     private final ErrorHandler errorHandler;
     private final ConsoleRenderer renderer;
     private final InputValidator validator;
     private final Scanner scanner = new Scanner(System.in);

     public void start() {

          while (true) {
               printMenu();

               String choice = scanner.nextLine();

               try {
                    switch (choice) {
                         case "1" -> createUser();
                         case "2" -> listUsers();
                         case "3" -> findUserByName();
                         case "4" -> getUser();
                         case "5" -> updateUser();
                         case "6" -> deleteUser();
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
                  3. Найти пользователя по имени
                  4. Найти пользователя по ID
                  5. Обновить пользователя
                  6. Удалить пользователя
                  0. Выход
                  Выберите пункт:\s""");
     }

     private void createUser() {
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

     private void getUser() {
          System.out.print("Введите ID: ");
          UUID id = validator.readUUID(scanner);
          log.info("Called ConsoleMenu.getUser() with parameter: id={}", id);

          var result = userService.getUser(id);
          renderer.render(result);
     }

     private void updateUser() {
          UUID id = chooseUserByName();
          if (id == null) {
               return;
          }

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

     private void deleteUser() {
          UUID id = chooseUserByName();

          log.info("Called ConsoleMenu.deleteUser() with parameter id={}", id);
          if (id == null) {
               return;
          }

          var result = userService.deleteUser(id);
          renderer.render(result);
     }

     private void findUserByName() {
          UUID id = chooseUserByName();
          log.info("Called ConsoleMenu.findUserByName() with parameter id={}", id);
          if (id == null) {
               return;
          }

          var result = userService.getUser(id);
          renderer.render(result);
     }

     private UUID chooseUserByName() {
          System.out.print("Введите имя или часть имени: ");
          String name = scanner.nextLine().trim();

          var result = userService.searchUsersByName(name);

          if (result.status() != Status.SUCCESS) {
               renderer.render(result);
               return null;
          }

          var users = result.data();
          renderer.renderIndexedUsers(users);

          System.out.print("Выберите номер пользователя: ");
          int index = validator.readIndex(scanner, users.size());

          return users.get(index).getId();
     }
}

