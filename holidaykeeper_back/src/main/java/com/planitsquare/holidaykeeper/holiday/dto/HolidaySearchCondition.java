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
}
