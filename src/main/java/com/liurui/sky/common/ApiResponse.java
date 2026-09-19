package com.liurui.sky.common;

public record ApiResponse<T>(Integer code, String msg, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(1, "success", data);
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(0, message, null);
    }
}
