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

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HolidayCustomRepositoryImpl implements HolidayCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HolidayResponseDto> search(HolidaySearchCondition cond, Pageable pageable) {

        QHoliday h = QHoliday.holiday;
        QCountry c = QCountry.country;

        List<HolidayResponseDto> content = queryFactory
                .select(Projections.constructor(
                        HolidayResponseDto.class,
                        h.id,
                        c.countryCode,
                        h.date,
                        h.holidayYear,
                        h.localName,
                        h.englishName,
                        h.fixedHoliday,
                        h.globalHoliday,
                        h.launchYear,
                        h.types
                ))
                .from(h)
                .join(h.country, c)
                .where(
                        yearEq(cond.getYear()),
                        countryCodeEq(cond.getCountryCode()),
                        dateGoe(cond.getFrom()),
                        dateLoe(cond.getTo()),
                        typeContains(cond.getType()),
                        localNameContains(cond.getLocalNameKeyword()),
                        englishNameContains(cond.getEnglishNameKeyword()),
                        fixedHolidayEq(cond.getFixedHoliday()),
                        globalHolidayEq(cond.getGlobalHoliday()),
                        launchYearGoe(cond.getLaunchYearFrom()),
                        launchYearLoe(cond.getLaunchYearTo())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(h.date.asc())
                .fetch();

        var countQuery = queryFactory
                .select(h.count())
                .from(h)
                .join(h.country, c)
                .where(
                        yearEq(cond.getYear()),
                        countryCodeEq(cond.getCountryCode()),
                        dateGoe(cond.getFrom()),
                        dateLoe(cond.getTo()),
                        typeContains(cond.getType()),
                        localNameContains(cond.getLocalNameKeyword()),
                        englishNameContains(cond.getEnglishNameKeyword()),
                        fixedHolidayEq(cond.getFixedHoliday()),
                        globalHolidayEq(cond.getGlobalHoliday()),
                        launchYearGoe(cond.getLaunchYearFrom()),
                        launchYearLoe(cond.getLaunchYearTo())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private BooleanExpression yearEq(Integer year) {
        return year != null ? QHoliday.holiday.holidayYear.eq(year) : null;
    }

    private BooleanExpression countryCodeEq(String code) {
        return (code != null && !code.isBlank())
                ? QCountry.country.countryCode.eq(code)
                : null;
    }

    private BooleanExpression dateGoe(LocalDate from) {
        return from != null ? QHoliday.holiday.date.goe(from) : null;
    }

    private BooleanExpression dateLoe(LocalDate to) {
        return to != null ? QHoliday.holiday.date.loe(to) : null;
    }

    private BooleanExpression typeContains(String type) {
        return (type != null && !type.isBlank())
                ? QHoliday.holiday.types.containsIgnoreCase(type)
                : null;
    }

    private BooleanExpression localNameContains(String keyword) {
        return (keyword != null && !keyword.isBlank())
                ? QHoliday.holiday.localName.containsIgnoreCase(keyword)
                : null;
    }

    private BooleanExpression englishNameContains(String keyword) {
        return (keyword != null && !keyword.isBlank())
                ? QHoliday.holiday.englishName.containsIgnoreCase(keyword)
                : null;
    }

    private BooleanExpression fixedHolidayEq(Boolean fixed) {
        return fixed != null ? QHoliday.holiday.fixedHoliday.eq(fixed) : null;
    }

    private BooleanExpression globalHolidayEq(Boolean global) {
        return global != null ? QHoliday.holiday.globalHoliday.eq(global) : null;
    }

    private BooleanExpression launchYearGoe(Integer year) {
        return year != null ? QHoliday.holiday.launchYear.goe(year) : null;
    }

    private BooleanExpression launchYearLoe(Integer year) {
        return year != null ? QHoliday.holiday.launchYear.loe(year) : null;
    }
}
