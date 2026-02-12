package ui;

import dto.Result;
import entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ConsoleRenderer {

     public <T> void render(Result<T> result) {
          switch (result.status()) {
               case SUCCESS -> {
                    log.info("Rendering SUCCESS: data={}", result.data());
                    System.out.println("Успех: " + result.data());
               }
               case NOT_FOUND -> {
                    log.info("Rendering NOT_FOUND: message={}", result.message());
                    System.out.println("Не найдено: " + result.message());
               }
               case VALIDATION_ERROR -> {
                    log.warn("Rendering VALIDATION_ERROR: message={}", result.message());
                    System.out.println("Некорректный ввод: " + result.message());
               }
               case FAILURE -> {
                    log.error("Rendering FAILURE: message={}", result.message());
                    System.out.println("Ошибка: " + result.message());
               }
          }
     }


     public <T> void renderList(List<T> list) {
          if (list.isEmpty()) {
               System.out.println("Список пуст.");
               log.info("An empty list was printed to the console");
          } else {
               log.info("A list of {} elements was printed to the console", list.size());
               list.forEach(System.out::println);
          }
     }

     public void renderIndexedUsers(List<User> users) {
          System.out.printf("Найдено %d пользователей:%n", users.size());
          int index = 1;
          for (User u : users) {
               System.out.printf("%d. %s (id=%s)%n", index++, u.getName(), u.getId());
          }
     }
}

