package com.planitsquare.holidaykeeper.holiday.repository;

import com.planitsquare.holidaykeeper.country.entity.Country;
import com.planitsquare.holidaykeeper.country.repository.CountryRepository;
import com.planitsquare.holidaykeeper.holiday.dto.HolidayResponseDto;
import com.planitsquare.holidaykeeper.holiday.dto.HolidaySearchCondition;
import com.planitsquare.holidaykeeper.holiday.entity.Holiday;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(HolidayRepositoryTest.QuerydslTestConfig.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class HolidayRepositoryTest {

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    CountryRepository countryRepository;

    @Test
    @DisplayName("year + countryCode + from~to 조건으로 공휴일 검색")
    void searchHolidays_withConditions() {
        // given
        Country kr = new Country();
        kr.setCountryCode("KR");
        kr.setName("Korea");
        countryRepository.save(kr);

        Holiday h1 = new Holiday();
        h1.setCountry(kr);
        h1.setDate(LocalDate.of(2024, 3, 1));
        h1.setHolidayYear(2024);
        h1.setLocalName("3·1절");
        h1.setEnglishName("Independence Movement Day");
        h1.setFixedHoliday(false);
        h1.setGlobalHoliday(true);
        h1.setTypes("Public");
        holidayRepository.save(h1);

        Holiday h2 = new Holiday();
        h2.setCountry(kr);
        h2.setDate(LocalDate.of(2024, 5, 5));
        h2.setHolidayYear(2024);
        h2.setLocalName("어린이날");
        h2.setEnglishName("Children's Day");
        h2.setFixedHoliday(false);
        h2.setGlobalHoliday(true);
        h2.setTypes("Public");
        holidayRepository.save(h2);

        HolidaySearchCondition condition = HolidaySearchCondition.builder()
                .year(2024)
                .countryCode("KR")
                .from(LocalDate.of(2024, 3, 1))
                .to(LocalDate.of(2024, 12, 31))
                .type("Public")
                .build();

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<HolidayResponseDto> page = holidayRepository.search(condition, pageable);

        // then
        // --- 기존 검증 ---
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent())
                .extracting("countryCode")
                .containsOnly("KR");
        assertThat(page.getContent())
                .extracting("holidayYear")
                .containsOnly(2024);

        assertThat(page.getNumber()).isEqualTo(0);          // page
        assertThat(page.getSize()).isEqualTo(10);           // size
        assertThat(page.getTotalPages()).isEqualTo(1);      // totalPages
        assertThat(page.isLast()).isTrue();                 // last page 여부
    }

    @Test
    @DisplayName("localName/englishName + fixed/global + launchYear 범위 필터링")
    void searchHolidays_withDetailFilters() {
        // given
        Country kr = new Country();
        kr.setCountryCode("KR");
        kr.setName("Korea");
        countryRepository.save(kr);

        // 1) 3·1절 (조건에 안 걸리게 일부러 세팅)
        Holiday h1 = new Holiday();
        h1.setCountry(kr);
        h1.setDate(LocalDate.of(2024, 3, 1));
        h1.setHolidayYear(2024);
        h1.setLocalName("3·1절");
        h1.setEnglishName("Independence Movement Day");
        h1.setFixedHoliday(false);
        h1.setGlobalHoliday(true);
        h1.setLaunchYear(1949);
        h1.setTypes("Public");
        holidayRepository.save(h1);

        // 2) 크리스마스 (우리가 잡으려는 타겟 데이터)
        Holiday h2 = new Holiday();
        h2.setCountry(kr);
        h2.setDate(LocalDate.of(2024, 12, 25));
        h2.setHolidayYear(2024);
        h2.setLocalName("크리스마스");
        h2.setEnglishName("Christmas Day");
        h2.setFixedHoliday(true);
        h2.setGlobalHoliday(true);
        h2.setLaunchYear(1900);
        h2.setTypes("Public");
        holidayRepository.save(h2);

        HolidaySearchCondition condition = HolidaySearchCondition.builder()
                .year(2024)
                .countryCode("KR")
                .localNameKeyword("리스마스")        // localNameContains
                .englishNameKeyword("Christ")       // englishNameContains
                .fixedHoliday(true)                 // fixedHolidayEq
                .globalHoliday(true)                // globalHolidayEq
                .launchYearFrom(1890)               // launchYearGoe
                .launchYearTo(1950)                 // launchYearLoe
                .build();

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<HolidayResponseDto> page = holidayRepository.search(condition, pageable);

        // then
        assertThat(page.getTotalElements()).isEqualTo(1);
        HolidayResponseDto result = page.getContent().get(0);

        assertThat(result.localName()).isEqualTo("크리스마스");
        assertThat(result.englishName()).isEqualTo("Christmas Day");
        assertThat(result.fixedHoliday()).isTrue();
        assertThat(result.globalHoliday()).isTrue();
        assertThat(result.launchYear()).isBetween(1890, 1950);
    }
    
    // Querydsl용 JPAQueryFactory 주입
    static class QuerydslTestConfig {

        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }
}
