package com.planitsquare.holidaykeeper.country.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planitsquare.holidaykeeper.country.dto.CountryDto;
import com.planitsquare.holidaykeeper.country.service.CountryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CountryController.class)
class CountryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CountryService countryService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("전체 국가 목록 조회 API")
    void getAllCountries() throws Exception {
        // given
        List<CountryDto> countries = List.of(
                new CountryDto("KR", "Korea"),
                new CountryDto("US", "United States")
        );

        given(countryService.getAllCountries())
                .willReturn(countries);

        // when & then
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].countryCode").value("KR"))
                .andExpect(jsonPath("$.data[0].name").value("Korea"));
    }

    @Test
    @DisplayName("국가 이름 검색 API")
    void searchCountries() throws Exception {
        // given
        List<CountryDto> result = List.of(
                new CountryDto("KR", "Korea")
        );

        given(countryService.searchCountries(anyString()))
                .willReturn(result);

        // when & then
        mockMvc.perform(get("/api/countries/search")
                        .param("keyword", "ko"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].countryCode").value("KR"));
    }

    @Test
    @DisplayName("단일 국가 조회 API")
    void getCountry() throws Exception {
        // given
        CountryDto dto = new CountryDto("KR", "Korea");

        given(countryService.getCountry("KR"))
                .willReturn(dto);

        // when & then
        mockMvc.perform(get("/api/countries/KR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.countryCode").value("KR"));
    }
}
