package com.planitsquare.holidaykeeper.holiday.service;

import com.planitsquare.holidaykeeper.country.entity.Country;
import com.planitsquare.holidaykeeper.country.repository.CountryRepository;
import com.planitsquare.holidaykeeper.dateapi.client.DateApiClient;
import com.planitsquare.holidaykeeper.global.exception.BusinessException;
import com.planitsquare.holidaykeeper.global.exception.ErrorCode;
import com.planitsquare.holidaykeeper.holiday.dto.HolidayResponseDto;
import com.planitsquare.holidaykeeper.holiday.dto.HolidaySearchCondition;
import com.planitsquare.holidaykeeper.holiday.entity.Holiday;
import com.planitsquare.holidaykeeper.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final CountryRepository countryRepository;
    private final DateApiClient dateApiClient;

    @Override
    @Transactional(readOnly = true)
    public Page<HolidayResponseDto> searchHolidays(HolidaySearchCondition condition, Pageable pageable) {
        //연도 검증
        validateYear(condition.getYear());
        return holidayRepository.search(condition, pageable);
    }

    @Override
    public void refreshYearAndCountry(Integer year, String countryCode) {
        //연도 검증
        validateYear(year);

        Country country = countryRepository.findById(countryCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUNTRY_NOT_FOUND));

        List<Holiday> refreshed;
        try {
            refreshed = dateApiClient.getHolidays(year, countryCode);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
        }

        // 기존 데이터 삭제
        holidayRepository.deleteByHolidayYearAndCountry_CountryCode(year, countryCode);

        // 신규 데이터 저장
        for (Holiday h : refreshed) {
            h.setCountry(country);
            h.setHolidayYear(year);
            holidayRepository.save(h);
        }
    }

    @Override
    public void deleteByYearAndCountry(Integer year, String countryCode) {
        validateYear(year);
        holidayRepository.deleteByHolidayYearAndCountry_CountryCode(year, countryCode);
    }

    @Override
    public void loadInitialData() {
        List<Country> countries = countryRepository.findAll();

        int currentYear = LocalDate.now().getYear();
        int startYear = currentYear - 5; // 최근 6년 ex 2020~2025

        for (int year = startYear; year <= currentYear; year++) {
            for (Country country : countries) {
                refreshYearAndCountry(year, country.getCountryCode());
            }
        }
    }

    private void validateYear(Integer year) {
        int currentYear = LocalDate.now().getYear();
        if (year == null) return;
        if (year <= currentYear-5 || year >= currentYear) {
            throw new BusinessException(ErrorCode.INVALID_YEAR_RANGE,currentYear-5,currentYear);
        }
    }
}
