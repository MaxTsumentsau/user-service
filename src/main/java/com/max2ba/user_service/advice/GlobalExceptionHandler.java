package com.max2ba.user_service.advice;

import com.max2ba.user_service.annotation.Loggable;
import com.max2ba.user_service.dto.ResponseCode;
import com.max2ba.user_service.dto.ApiResponse;
import com.max2ba.user_service.exception.NotFoundException;
import com.max2ba.user_service.exception.ValidationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Loggable
@RestControllerAdvice
public class GlobalExceptionHandler {

     @ExceptionHandler(NotFoundException.class)
     public ResponseEntity<?> handleNotFound(NotFoundException ex) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(ApiResponse.error(ResponseCode.USER_NOT_FOUND_ERROR, ex.getMessage()));
     }

     @ExceptionHandler(ValidationException.class)
     public ResponseEntity<?> handleValidation(ValidationException ex) {
          return ResponseEntity.badRequest()
                  .body(ApiResponse.error(ResponseCode.EMAIL_VALIDATION_ERROR, ex.getMessage()));
     }

     @ExceptionHandler(DataIntegrityViolationException.class)
     public ResponseEntity<?> handleDbErrors(DataIntegrityViolationException ex) {
          return ResponseEntity.badRequest()
                  .body(ApiResponse.error(ResponseCode.DB_CONSTRAINT_ERROR, ex.getMessage()));
     }

     @ExceptionHandler(ConstraintViolationException.class)
     public ResponseEntity<?> handleHibernate(ConstraintViolationException ex) {
          return ResponseEntity.badRequest()
                  .body(ApiResponse.error(ResponseCode.DATA_CONSTRAINT_ERROR, ex.getMessage()));
     }

     @ExceptionHandler(NoResourceFoundException.class)
     public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException ex) {
          if (ex.getResourcePath().contains("favicon.ico")) {
               return ResponseEntity.notFound().build();
          }
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(ApiResponse.error(ResponseCode.RESOURCE_NOT_FOUND, ex.getMessage()));
     }

     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
          String details = ex.getBindingResult().getFieldErrors().stream()
                  .map(err -> err.getField() + ": " + err.getDefaultMessage())
                  .reduce((a, b) -> a + "; " + b)
                  .orElse("Validation error!");

          return ResponseEntity.badRequest().body(ApiResponse.error(ResponseCode.VALIDATION_ERROR, details));
     }

     @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
     public ResponseEntity<?> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
          return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                  .body(ApiResponse.error(ResponseCode.UNSUPPORTED_MEDIA_TYPE, ex.getMessage()));
     }


     @ExceptionHandler(MethodArgumentTypeMismatchException.class)
     public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
          return ResponseEntity.badRequest()
                  .body(ApiResponse.error(ResponseCode.INCORRECT_UUID_FORMAT, ex.getMessage()));
     }

     @ExceptionHandler(IllegalArgumentException.class)
     public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
          return ResponseEntity.badRequest()
                  .body(ApiResponse.error(ResponseCode.ILLEGAL_ARGUMENT_ERROR, ex.getMessage()));
     }

     @ExceptionHandler(RuntimeException.class)
     public ResponseEntity<?> handleRuntime(RuntimeException ex) {
          return ResponseEntity.internalServerError()
                  .body(ApiResponse.error(ResponseCode.INTERNAL_ERROR, ex.getMessage()));
     }

     @ExceptionHandler(Exception.class)
     public ResponseEntity<?> handleException(Exception ex) {
          return ResponseEntity.internalServerError()
                  .body(ApiResponse.error(ResponseCode.UNEXPECTED_ERROR, ex.getMessage()));
     }
}


