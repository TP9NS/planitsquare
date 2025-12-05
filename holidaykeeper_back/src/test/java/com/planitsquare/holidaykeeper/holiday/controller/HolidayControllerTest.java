package com.planitsquare.holidaykeeper.holiday.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planitsquare.holidaykeeper.global.dto.ResponseDto;
import com.planitsquare.holidaykeeper.holiday.dto.HolidayResponseDto;
import com.planitsquare.holidaykeeper.holiday.dto.HolidaySearchCondition;
import com.planitsquare.holidaykeeper.holiday.service.HolidayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HolidayController.class)
class HolidayControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    HolidayService holidayService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("공휴일 검색 API")
    void searchHolidays() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 20);

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

        Page<HolidayResponseDto> page =
                new PageImpl<>(List.of(dto), pageable, 1);

        given(holidayService.searchHolidays(any(HolidaySearchCondition.class), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/holidays")
                        .param("year", "2024")
                        .param("countryCode", "KR")
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].countryCode").value("KR"))
                .andExpect(jsonPath("$.data.content[0].holidayYear").value(2024));
    }
}
