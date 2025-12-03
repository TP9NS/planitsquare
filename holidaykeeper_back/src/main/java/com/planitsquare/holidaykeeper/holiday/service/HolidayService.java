package com.planitsquare.holidaykeeper.holiday.service;

import com.planitsquare.holidaykeeper.holiday.dto.HolidayResponseDto;
import com.planitsquare.holidaykeeper.holiday.dto.HolidaySearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayService {

    Page<HolidayResponseDto> searchHolidays(HolidaySearchCondition condition, Pageable pageable);

    void refreshYearAndCountry(Integer year, String countryCode);

    void deleteByYearAndCountry(Integer year, String countryCode);

    void loadInitialData();
}
