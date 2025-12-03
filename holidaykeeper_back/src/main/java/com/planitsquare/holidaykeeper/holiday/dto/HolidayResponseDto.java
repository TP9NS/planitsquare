package com.planitsquare.holidaykeeper.holiday.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record HolidayResponseDto(
        Long id,
        String countryCode,
        LocalDate date,
        Integer holidayYear,
        String localName,
        String englishName,
        boolean fixedHoliday,
        boolean globalHoliday,
        Integer launchYear,
        String types
) {}
