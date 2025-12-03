package com.planitsquare.holidaykeeper.runner;

import com.planitsquare.holidaykeeper.country.entity.Country;
import com.planitsquare.holidaykeeper.country.repository.CountryRepository;
import com.planitsquare.holidaykeeper.dateapi.client.DateApiClient;
import com.planitsquare.holidaykeeper.holiday.repository.HolidayRepository;
import com.planitsquare.holidaykeeper.holiday.service.HolidayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitRunner implements CommandLineRunner {

    private final CountryRepository countryRepository;
    private final HolidayRepository holidayRepository;
    private final DateApiClient dateApiClient;
    private final HolidayService holidayService;

    @Override
    public void run(String... args) {

        log.info("==== HolidayKeeper 초기 데이터 적재 시작 ====");

        if (countryRepository.count() == 0) {
            log.info("Country 외부 API 호출.");

            List<Country> countries = dateApiClient.getAvailableCountries();
            countryRepository.saveAll(countries);

            log.info("국가 저장 완료");
        } else {
            log.info("Country 테이블에 데이터가 존재)");
        }

        if (holidayRepository.count() == 0) {
            log.info("공휴일 일괄 적재를 시작");
            holidayService.loadInitialData();
            log.info("Holiday  적재 완료");
        } else {
            log.info("Holiday 테이블에 데이터가 존재");
        }

        log.info("==== HolidayKeeper 초기 데이터 적재 종료 ====");
    }
}
