package com.planitsquare.holidaykeeper.holiday.repository;

import com.planitsquare.holidaykeeper.holiday.dto.HolidayResponseDto;
import com.planitsquare.holidaykeeper.holiday.dto.HolidaySearchCondition;
import com.planitsquare.holidaykeeper.holiday.entity.QHoliday;
import com.planitsquare.holidaykeeper.country.entity.QCountry;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HolidayCustomRepositoryImpl implements HolidayCustomRepository { // ← 이름 통일

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HolidayResponseDto> search(HolidaySearchCondition condition, Pageable pageable) {

        QHoliday holiday = QHoliday.holiday;
        QCountry country = QCountry.country;

        List<HolidayResponseDto> content = queryFactory
                .select(Projections.constructor(
                        HolidayResponseDto.class,
                        holiday.id,
                        country.countryCode,
                        holiday.date,
                        holiday.holidayYear,
                        holiday.localName,
                        holiday.englishName,
                        holiday.fixedHoliday,
                        holiday.globalHoliday,
                        holiday.launchYear,
                        holiday.types
                ))
                .from(holiday)
                .join(holiday.country, country)
                .where(
                        holidayYearEq(condition.getYear()),
                        countryCodeEq(condition.getCountryCode()),
                        dateGoe(condition.getFrom()),
                        dateLoe(condition.getTo()),
                        typeContains(condition.getType())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(holiday.date.asc())
                .fetch();

        var countQuery = queryFactory
                .select(holiday.count())
                .from(holiday)
                .join(holiday.country, country)
                .where(
                        holidayYearEq(condition.getYear()),
                        countryCodeEq(condition.getCountryCode()),
                        dateGoe(condition.getFrom()),
                        dateLoe(condition.getTo()),
                        typeContains(condition.getType())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression holidayYearEq(Integer year) {
        return year != null ? QHoliday.holiday.holidayYear.eq(year) : null;
    }

    private BooleanExpression countryCodeEq(String countryCode) {
        return (countryCode != null && !countryCode.isBlank())
                ? QCountry.country.countryCode.eq(countryCode)
                : null;
    }

    private BooleanExpression dateGoe(java.time.LocalDate from) {
        return from != null ? QHoliday.holiday.date.goe(from) : null;
    }

    private BooleanExpression dateLoe(java.time.LocalDate to) {
        return to != null ? QHoliday.holiday.date.loe(to) : null;
    }

    private BooleanExpression typeContains(String type) {
        return (type != null && !type.isBlank())
                ? QHoliday.holiday.types.containsIgnoreCase(type)
                : null;
    }
}
