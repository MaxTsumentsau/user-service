package ui;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.SQLGrammarException;

@Slf4j
public class ErrorHandler {

     public void handle(Exception e) {

          Throwable cause = unwrap(e);

          log.error("Unhandled exception in UI layer: {} ({})",
                  cause.getMessage(), cause.getClass().getSimpleName(), cause);

          switch (cause) {
               case ConstraintViolationException cve -> {
                    log.error("Constraint violation: {}", cve.getMessage());
                    System.out.println("Ошибка: данные нарушают ограничения.");
                    return;
               }
               case JDBCConnectionException jdbcConnectionException -> {
                    log.error("JDBC Connection error: {}", jdbcConnectionException.getMessage());
                    System.out.println("Ошибка: нет соединения с базой данных.");
                    return;
               }
               case SQLGrammarException sqlGrammarException -> {
                    log.error("SQL grammar error: {}", sqlGrammarException.getMessage());
                    System.out.println("Ошибка: проблема с SQL запросом.");
                    return;
               }
               default -> {
               }
          }

          log.error("Unexpected error type");
          System.out.println("Произошла внутренняя ошибка. Попробуйте позже.");
     }

     private Throwable unwrap(Throwable e) {
          return e.getCause() != null ? e.getCause() : e;
     }
}


