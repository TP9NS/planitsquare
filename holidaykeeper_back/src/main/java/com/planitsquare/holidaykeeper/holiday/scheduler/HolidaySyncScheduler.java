// com.planitsquare.holidaykeeper.holiday.scheduler.HolidaySyncScheduler
package com.planitsquare.holidaykeeper.holiday.scheduler;

import com.planitsquare.holidaykeeper.country.entity.Country;
import com.planitsquare.holidaykeeper.country.repository.CountryRepository;
import com.planitsquare.holidaykeeper.holiday.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidaySyncScheduler {

    private final HolidayService holidayService;
    private final CountryRepository countryRepository;

    /**
     * 매년 1월 2일 01:00 KST에 전년도, 금년도 동기화
     */
    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    public void syncPrevAndCurrentYear() {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int currentYear = now.getYear();
        int prevYear = currentYear - 1;

        log.info("Holiday sync started. prevYear={}, currentYear={}", prevYear, currentYear);

        List<Country> countries = countryRepository.findAll();

        for (Country country : countries) {
            String code = country.getCountryCode();
            holidayService.refreshYearAndCountry(prevYear, code);
            holidayService.refreshYearAndCountry(currentYear, code);
        }

        log.info("Holiday sync finished.");
    }
}
