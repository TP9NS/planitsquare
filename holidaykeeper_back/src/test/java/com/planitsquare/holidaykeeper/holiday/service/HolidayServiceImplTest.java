package com.planitsquare.holidaykeeper.holiday.service;

import com.planitsquare.holidaykeeper.country.entity.Country;
import com.planitsquare.holidaykeeper.country.repository.CountryRepository;
import com.planitsquare.holidaykeeper.dateapi.client.DateApiClient;
import com.planitsquare.holidaykeeper.holiday.dto.HolidayResponseDto;
import com.planitsquare.holidaykeeper.holiday.dto.HolidaySearchCondition;
import com.planitsquare.holidaykeeper.holiday.entity.Holiday;
import com.planitsquare.holidaykeeper.holiday.repository.HolidayRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayServiceImplTest {

    @Mock
    HolidayRepository holidayRepository;

    @Mock
    CountryRepository countryRepository;

    @Mock
    DateApiClient dateApiClient;

    @InjectMocks
    HolidayServiceImpl holidayService;

    @Test
    @DisplayName("search Test")
    void searchHolidays_delegatesToRepository() {
        // given
        HolidaySearchCondition condition = HolidaySearchCondition.builder()
                .year(2024)
                .countryCode("KR")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        HolidayResponseDto dto = HolidayResponseDto.builder()
                .id(1L)
                .countryCode("KR")
                .date(LocalDate.of(2024, 3, 1))
                .holidayYear(2024)
                .localName("3·1절")
                .englishName("Independence Movement Day")
                .fixedHoliday(false)
                .globalHoliday(true)
                .types("Public")
                .build();

        Page<HolidayResponseDto> mockPage =
                new PageImpl<>(List.of(dto), pageable, 1);

        given(holidayRepository.search(condition, pageable))
                .willReturn(mockPage);

        // when
        Page<HolidayResponseDto> result =
                holidayService.searchHolidays(condition, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).countryCode()).isEqualTo("KR");
        then(holidayRepository).should().search(condition, pageable);
    }

    @Test
    @DisplayName("기존 데이터를 삭제 후 새 데이터를 저장")
    void refreshYearAndCountry_success() {
        // given
        int year = 2024;
        String countryCode = "KR";

        Country kr = new Country();
        kr.setCountryCode("KR");
        kr.setName("Korea");

        given(countryRepository.findById(countryCode))
                .willReturn(Optional.of(kr));

        Holiday h1 = new Holiday();
        h1.setDate(LocalDate.of(2024, 3, 1));

        Holiday h2 = new Holiday();
        h2.setDate(LocalDate.of(2024, 5, 5));

        given(dateApiClient.getHolidays(year, countryCode))
                .willReturn(List.of(h1, h2));

        // when
        holidayService.refreshYearAndCountry(year, countryCode);

        // then
        then(holidayRepository)
                .should()
                .deleteByHolidayYearAndCountry_CountryCode(year, countryCode);

        ArgumentCaptor<Holiday> captor = ArgumentCaptor.forClass(Holiday.class);
        then(holidayRepository)
                .should(times(2))
                .save(captor.capture());

        List<Holiday> saved = captor.getAllValues();

        assertThat(saved)
                .allSatisfy(h -> {
                    assertThat(h.getCountry()).isEqualTo(kr);
                    assertThat(h.getHolidayYear()).isEqualTo(year);
                });
    }

//    @Test
//    @DisplayName("없는 국가코드")
//    void refreshYearAndCountry_countryNotFound() {
//        int year = 2024;
//        String countryCode = "ZZ";
//
//        given(countryRepository.findById(countryCode))
//                .willReturn(Optional.empty());
//
//        assertThatThrownBy(() ->
//                holidayService.refreshYearAndCountry(year, countryCode)
//        ).isInstanceOf(IllegalArgumentException.class);
//    }
}
