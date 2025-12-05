package com.planitsquare.holidaykeeper.holiday.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class HolidaySearchCondition {
    private final Integer year;
    private final String countryCode;
    private final LocalDate from;
    private final LocalDate to;
    private final String type;
    private final String localNameKeyword;
    private final String englishNameKeyword;
    private final Boolean fixedHoliday;
    private final Boolean globalHoliday;
    private final Integer launchYearFrom;
    private final Integer launchYearTo;
}
