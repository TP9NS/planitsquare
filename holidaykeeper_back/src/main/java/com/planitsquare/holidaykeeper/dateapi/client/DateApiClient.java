// com.planitsquare.holidaykeeper.dateapi.client.DateApiClient
package com.planitsquare.holidaykeeper.dateapi.client;

import com.planitsquare.holidaykeeper.country.entity.Country;
import com.planitsquare.holidaykeeper.dateapi.dto.NagerCountryDto;
import com.planitsquare.holidaykeeper.dateapi.dto.NagerHolidayDto;
import com.planitsquare.holidaykeeper.holiday.entity.Holiday;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

@Component
public class DateApiClient {

    private final RestClient restClient;

    public DateApiClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://date.nager.at/api/v3")
                .build();
    }

    public List<Holiday> getHolidays(int year, String countryCode) {
        List<NagerHolidayDto> dtos = restClient.get()
                .uri("/PublicHolidays/{year}/{countryCode}", year, countryCode)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return dtos.stream()
                .map(this::toHolidayEntity)
                .toList();
    }

    public List<Country> getAvailableCountries() {
        List<NagerCountryDto> dtos = restClient.get()
                .uri("/AvailableCountries")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return dtos.stream()
                .map(dto -> {
                    Country c = new Country();
                    c.setCountryCode(dto.getCountryCode());
                    c.setName(dto.getName());
                    return c;
                })
                .toList();
    }

    private Holiday toHolidayEntity(NagerHolidayDto dto) {
        Holiday h = new Holiday();
        h.setDate(LocalDate.parse(dto.getDate()));
        h.setLocalName(dto.getLocalName());
        h.setEnglishName(dto.getName());
        h.setFixedHoliday(dto.isFixed());
        h.setGlobalHoliday(dto.isGlobal());
        h.setLaunchYear(dto.getLaunchYear());

        if (dto.getTypes() != null && !dto.getTypes().isEmpty()) {
            h.setTypes(String.join(",", dto.getTypes()));
        }
        return h;
    }
}
