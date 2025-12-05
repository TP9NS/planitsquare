package com.planitsquare.holidaykeeper.country.dto;

import com.planitsquare.holidaykeeper.country.entity.Country;

public record CountryDto(
        String countryCode,
        String name
) {
    public static CountryDto from(Country country) {
        return new CountryDto(
                country.getCountryCode(),
                country.getName()
        );
    }
}
