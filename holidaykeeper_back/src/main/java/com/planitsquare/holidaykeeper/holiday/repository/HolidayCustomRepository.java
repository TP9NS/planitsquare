package com.planitsquare.holidaykeeper.holiday.repository;

import com.planitsquare.holidaykeeper.holiday.dto.HolidayResponseDto;
import com.planitsquare.holidaykeeper.holiday.dto.HolidaySearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayCustomRepository {
    Page<HolidayResponseDto> search(HolidaySearchCondition condition, Pageable pageable);
}
