package com.max2ba.user_service.advice;

import com.max2ba.user_service.annotation.Loggable;
import com.max2ba.user_service.exception.NotFoundException;
import com.max2ba.user_service.exception.ValidationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Loggable
@RestControllerAdvice
public class GlobalExceptionHandler {
     private static final String DB_CONSTRAINT_ERROR = "Нарушение ограничений БД. ";
     private static final String DATA_CONSTRAINT_ERROR = "Нарушение ограничений данных. ";
     private static final String RESOURCE_NOT_FOUND = "Ресурс не найден: ";
     private static final String INCORRECT_UUID_FORMAT = "Некорректный формат UUID";


     @ExceptionHandler(NotFoundException.class)
     public ResponseEntity<?> handleNotFound(NotFoundException ex) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", ex.getMessage()));
     }

     @ExceptionHandler(ValidationException.class)
     public ResponseEntity<?> handleValidation(ValidationException ex) {
          return ResponseEntity.badRequest()
                  .body(Map.of("error", ex.getMessage()));
     }

     @ExceptionHandler(DataIntegrityViolationException.class)
     public ResponseEntity<?> handleDbErrors(DataIntegrityViolationException ex) {
          return ResponseEntity.badRequest()
                  .body(Map.of("error", DB_CONSTRAINT_ERROR + ex.getMessage()));
     }

     @ExceptionHandler(ConstraintViolationException.class)
     public ResponseEntity<?> handleHibernate(ConstraintViolationException ex) {
          return ResponseEntity.badRequest()
                  .body(Map.of("error", DATA_CONSTRAINT_ERROR + ex.getMessage()));
     }

     @ExceptionHandler(NoResourceFoundException.class)
     public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException ex) {
          if (ex.getResourcePath().contains("favicon.ico")) {
               return ResponseEntity.notFound().build();
          }
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", RESOURCE_NOT_FOUND + ex.getMessage()));
     }

     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
          Map<String, String> errors = new HashMap<>();
          ex.getBindingResult().getFieldErrors().forEach(error ->
                  errors.put(error.getField(), error.getDefaultMessage())
          );
          return ResponseEntity.badRequest().body(errors);
     }

     @ExceptionHandler(MethodArgumentTypeMismatchException.class)
     public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
          return ResponseEntity.badRequest()
                  .body(Map.of("error", INCORRECT_UUID_FORMAT));
     }

     @ExceptionHandler(IllegalArgumentException.class)
     public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
          return ResponseEntity.badRequest()
                  .body(Map.of("error", ex.getMessage()));
     }

     @ExceptionHandler(Exception.class)
     public ResponseEntity<?> handleOther(Exception ex) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(Map.of("error", ex.getMessage()));
     }
}


