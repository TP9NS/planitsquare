package com.planitsquare.holidaykeeper.country.repository;

import com.planitsquare.holidaykeeper.country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
    // countryCode가 PK라고 가정 (String)
}