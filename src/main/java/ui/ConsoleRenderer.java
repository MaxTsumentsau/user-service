package ui;

import dto.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ConsoleRenderer {

     public <T> void render(Result<T> result) {
          switch (result.status()) {
               case SUCCESS -> {
                    log.info("Rendering SUCCESS result to console: data={}", result.data());
                    System.out.println("Успех: " + result.data());
               }
               case NOT_FOUND, VALIDATION_ERROR, FAILURE -> {
                    log.warn("Rendering ERROR result to console: status={}, message={}", result.status(), result.message());
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
}

