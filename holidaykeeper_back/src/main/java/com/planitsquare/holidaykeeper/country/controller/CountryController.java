package com.planitsquare.holidaykeeper.country.controller;

import com.planitsquare.holidaykeeper.country.dto.CountryDto;
import com.planitsquare.holidaykeeper.country.service.CountryService;
import com.planitsquare.holidaykeeper.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    // 전체 목록
    @GetMapping
    public ResponseDto<List<CountryDto>> getAllCountries() {
        return ResponseDto.success(countryService.getAllCountries());
    }

    // 이름 검색
    @GetMapping("/search")
    public ResponseDto<List<CountryDto>> searchCountries(@RequestParam String keyword) {
        return ResponseDto.success(countryService.searchCountries(keyword));
    }

    // 단일 조회
    @GetMapping("/{countryCode}")
    public ResponseDto<CountryDto> getCountry(@PathVariable String countryCode) {
        return ResponseDto.success(countryService.getCountry(countryCode));
    }
}
