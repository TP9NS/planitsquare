// com.planitsquare.holidaykeeper.global.exception.GlobalExceptionHandler
package com.planitsquare.holidaykeeper.global.exception;

import com.planitsquare.holidaykeeper.global.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto<Void>> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("BusinessException: {}, message={}", errorCode, ex.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ResponseDto.error(errorCode, ex.getMessage()));
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDto<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {

        log.warn("MethodArgumentTypeMismatchException: param={}, value={}, requiredType={}",
                ex.getName(), ex.getValue(), ex.getRequiredType(), ex);

        if (ex.getRequiredType() == LocalDate.class || ex.getCause() instanceof DateTimeParseException) {
            return ResponseEntity
                    .status(ErrorCode.INVALID_DATE.getStatus())
                    .body(ResponseDto.error(ErrorCode.INVALID_DATE));
        }

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ResponseDto.error(errorCode));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseDto<Void>> handleNoResource(NoResourceFoundException ex) {
        log.warn("NoResourceFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(404)
                .body(ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleException(Exception ex) {
        log.error("Unexpected exception", ex);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ResponseDto.error(errorCode));
    }
}
