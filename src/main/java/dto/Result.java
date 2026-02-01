package dto;

public record Result<T>(
        Status status,
        T data,
        String message
) {

     public static <T> Result<T> success(T data) {
          return new Result<>(Status.SUCCESS, data, null);
     }

     public static <T> Result<T> notFound(String message) {
          return new Result<>(Status.NOT_FOUND, null, message);
     }

     public static <T> Result<T> validationError(String message) {
          return new Result<>(Status.VALIDATION_ERROR, null, message);
     }

     public static <T> Result<T> failure(String message) {
          return new Result<>(Status.FAILURE, null, message);
     }
}

