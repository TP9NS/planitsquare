package com.planitsquare.holidaykeeper.holiday.controller;

import com.planitsquare.holidaykeeper.global.dto.PageResponse;
import com.planitsquare.holidaykeeper.global.dto.ResponseDto;
import com.planitsquare.holidaykeeper.global.exception.BusinessException;
import com.planitsquare.holidaykeeper.global.exception.ErrorCode;
import com.planitsquare.holidaykeeper.holiday.dto.HolidayResponseDto;
import com.planitsquare.holidaykeeper.holiday.dto.HolidaySearchCondition;
import com.planitsquare.holidaykeeper.holiday.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;
    // 공휴일 검색 + 필터링 (페이징)
    @GetMapping
    public ResponseEntity<ResponseDto<PageResponse<HolidayResponseDto>>> searchHolidays(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String localNameKeyword,
            @RequestParam(required = false) String englishNameKeyword,
            @RequestParam(required = false) Boolean fixedHoliday,
            @RequestParam(required = false) Boolean globalHoliday,
            @RequestParam(required = false) Integer launchYearFrom,
            @RequestParam(required = false) Integer launchYearTo,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        if (year != null && (from != null || to != null)) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);

        }
        if (year != null && from == null && to == null) {
            from = LocalDate.of(year, 1, 1);
            to = LocalDate.of(year, 12, 31);
        }
        HolidaySearchCondition condition = HolidaySearchCondition.builder()
                .year(year)
                .countryCode(countryCode)
                .from(from)
                .to(to)
                .type(type)
                .localNameKeyword(localNameKeyword)
                .englishNameKeyword(englishNameKeyword)
                .fixedHoliday(fixedHoliday)
                .globalHoliday(globalHoliday)
                .launchYearFrom(launchYearFrom)
                .launchYearTo(launchYearTo)
                .build();

        Page<HolidayResponseDto> result = holidayService.searchHolidays(condition, pageable);
        return ResponseEntity.ok(
                ResponseDto.success(PageResponse.from(result))
        );
    }
    // 특정 연도 + 국가 코드 삭제 후 다시 불러오기 (재동기화)
    @PutMapping("/refresh")
    public ResponseEntity<ResponseDto<Void>> refreshHolidays(
            @RequestParam Integer year,
            @RequestParam String countryCode
    ) {
        holidayService.refreshYearAndCountry(year, countryCode);
        return ResponseEntity.ok(ResponseDto.success(null));
    }
    //특정 연도 + 국가 코드 삭제
    @DeleteMapping
    public ResponseEntity<ResponseDto<Void>> deleteHolidays(
            @RequestParam Integer year,
            @RequestParam String countryCode
    ) {
        holidayService.deleteByYearAndCountry(year, countryCode);
        return ResponseEntity.ok(ResponseDto.success(null));
    }
//    수동 초기화 사용 x
//    @PostMapping("/init")
//    public ResponseEntity<ResponseDto<Void>> initLoad() {
//        holidayService.loadInitialData();
//        return ResponseEntity.ok(ResponseDto.success(null));
//    }
}
