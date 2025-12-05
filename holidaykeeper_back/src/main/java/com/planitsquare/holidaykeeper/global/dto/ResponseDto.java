package com.planitsquare.holidaykeeper.global.dto;

import com.planitsquare.holidaykeeper.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ResponseDto<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> ResponseDto<T> success(T data) {
        return ResponseDto.<T>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ResponseDto<T> success(String message, T data) {
        return ResponseDto.<T>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseDto<T> error(ErrorCode errorCode) {
        return ResponseDto.<T>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }

    public static <T> ResponseDto<T> error(ErrorCode errorCode, T data) {
        return ResponseDto.<T>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> ResponseDto<T> error(ErrorCode errorCode, String message) {
        return new ResponseDto<>(
                errorCode.getStatus(),
                errorCode.getCode(),
                message,
                null
        );
    }

}
