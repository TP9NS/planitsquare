package com.planitsquare.holidaykeeper.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public BusinessException(ErrorCode errorCode,int start , int end) {
        super(String.format(errorCode.getMessage(),start,end));
        this.errorCode = errorCode;
    }
}
