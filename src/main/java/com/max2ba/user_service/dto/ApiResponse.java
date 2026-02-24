package com.max2ba.user_service.dto;


public record ApiResponse<T>(
        ResponseCode code,
        String message,
        T data
) {
     public static <T> ApiResponse<T> success(T data) {
          return new ApiResponse<>(ResponseCode.SUCCESS, ResponseCode.SUCCESS.message(), data);
     }

     public static <T> ApiResponse<String> error(ResponseCode code, String details) {
          return new ApiResponse<>(code, code.message(), details);
     }
}
