package com.planitsquare.holidaykeeper.country.repository;

import com.planitsquare.holidaykeeper.country.entity.Country;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CountryRepositoryTest.QuerydslTestConfig.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})

class CountryRepositoryTest {

    @Autowired
    CountryRepository countryRepository;

    @Test
    @DisplayName("전체 국가 조회")
    void findAllCountries() {
        // given
        Country kr = new Country();
        kr.setCountryCode("KR");
        kr.setName("Korea");

        Country us = new Country();
        us.setCountryCode("US");
        us.setName("United States");

        countryRepository.save(kr);
        countryRepository.save(us);

        // when
        List<Country> countries = countryRepository.findAll();

        // then
        assertThat(countries).hasSize(2);
    }

    @Test
    @DisplayName("이름으로 부분 검색 (ignoreCase)")
    void searchByName() {
        // given
        Country kr = new Country();
        kr.setCountryCode("KR");
        kr.setName("Korea");

        countryRepository.save(kr);

        // when
        List<Country> result = countryRepository.findByNameContainingIgnoreCase("ko");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Korea");
    }

    static class QuerydslTestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }
}
