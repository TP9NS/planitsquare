package com.planitsquare.holidaykeeper.holiday.repository;

import com.planitsquare.holidaykeeper.holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayCustomRepository {

    void deleteByHolidayYearAndCountry_CountryCode(Integer holidayYear, String countryCode);
}
