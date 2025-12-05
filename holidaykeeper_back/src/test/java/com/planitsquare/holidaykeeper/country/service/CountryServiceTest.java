package com.planitsquare.holidaykeeper.country.service;

import com.planitsquare.holidaykeeper.country.dto.CountryDto;
import com.planitsquare.holidaykeeper.country.entity.Country;
import com.planitsquare.holidaykeeper.country.repository.CountryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    CountryRepository countryRepository;

    @InjectMocks
    CountryService countryService;

    @Test
    @DisplayName("전체 국가 조회")
    void getAllCountries() {
        // given
        Country kr = new Country();
        kr.setCountryCode("KR");
        kr.setName("Korea");

        given(countryRepository.findAll())
                .willReturn(List.of(kr));

        // when
        List<CountryDto> result = countryService.getAllCountries();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).countryCode()).isEqualTo("KR");
        then(countryRepository).should().findAll();
    }

    @Test
    @DisplayName("국가 이름 검색")
    void searchCountries() {
        // given
        Country kr = new Country();
        kr.setCountryCode("KR");
        kr.setName("Korea");

        given(countryRepository.findByNameContainingIgnoreCase("ko"))
                .willReturn(List.of(kr));

        // when
        List<CountryDto> result = countryService.searchCountries("ko");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Korea");
        then(countryRepository).should().findByNameContainingIgnoreCase("ko");
    }

    @Test
    @DisplayName("국가 코드 단일 조회")
    void getCountry() {
        // given
        Country kr = new Country();
        kr.setCountryCode("KR");
        kr.setName("Korea");

        given(countryRepository.findById("KR"))
                .willReturn(Optional.of(kr));

        // when
        CountryDto dto = countryService.getCountry("KR");

        // then
        assertThat(dto.countryCode()).isEqualTo("KR");
        then(countryRepository).should().findById("KR");
    }
}
