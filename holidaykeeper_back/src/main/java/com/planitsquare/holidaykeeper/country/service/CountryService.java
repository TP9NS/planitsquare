package com.planitsquare.holidaykeeper.country.service;

import com.planitsquare.holidaykeeper.country.dto.CountryDto;
import com.planitsquare.holidaykeeper.country.entity.Country;
import com.planitsquare.holidaykeeper.country.repository.CountryRepository;
import com.planitsquare.holidaykeeper.global.exception.BusinessException;
import com.planitsquare.holidaykeeper.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {

    private final CountryRepository countryRepository;

    // 전체 목록
    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll()
                .stream()
                .map(CountryDto::from)
                .toList();
    }

    // 이름 검색 (부분 일치)
    public List<CountryDto> searchCountries(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllCountries();
        }

        return countryRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(CountryDto::from)
                .toList();
    }

    public CountryDto getCountry(String countryCode) {
        Country country = countryRepository.findById(countryCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUNTRY_NOT_FOUND));
        return CountryDto.from(country);
    }
}
