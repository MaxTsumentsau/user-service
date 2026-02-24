package com.max2ba.user_service.dto;

public enum ResponseCode {
     SUCCESS("Запрос успешно выполнен. "),
     VALIDATION_ERROR("Некорректные данные запроса. "),
     UNSUPPORTED_MEDIA_TYPE("Неподдерживаемый Content-Type. "),
     INTERNAL_ERROR("Внутренняя ошибка сервера. "),
     UNEXPECTED_ERROR("Непредвиденная ошибка. "),
     EMAIL_VALIDATION_ERROR("Имейл уже существует. "),
     DB_CONSTRAINT_ERROR("Нарушение ограничений БД. "),
     DATA_CONSTRAINT_ERROR("Нарушение ограничений данных. "),
     RESOURCE_NOT_FOUND("Ресурс не найден. "),
     INCORRECT_UUID_FORMAT("Некорректный формат UUID. "),
     ILLEGAL_ARGUMENT_ERROR("Некорректный аргумент. "),
     USER_NOT_FOUND_ERROR("Пользователь не найден. ");

     private final String defaultMessage;

     ResponseCode(String defaultMessage) {
          this.defaultMessage = defaultMessage;
     }

     public String message() {
          return defaultMessage;
     }
}


