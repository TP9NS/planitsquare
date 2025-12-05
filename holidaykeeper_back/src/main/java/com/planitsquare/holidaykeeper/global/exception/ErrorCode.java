// com.planitsquare.holidaykeeper.global.exception.ErrorCode
package com.planitsquare.holidaykeeper.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Getter
public enum ErrorCode {

    INVALID_YEAR_RANGE(HttpStatus.BAD_REQUEST, "YEAR_001", "연도는 %d 이상 %d 이하만 허용됩니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "DATE_001", "잘못된 날짜 형식입니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST,"DATE_002","연도와 기간은 동시에 설정이 불가능합니다."),
    COUNTRY_NOT_FOUND(HttpStatus.NOT_FOUND, "COUNTRY_001", "해당 국가 코드를 찾을 수 없습니다."),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "API_001", "외부 공휴일 API 호출 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status.value();
        this.code = code;
        this.message = message;
    }
}
