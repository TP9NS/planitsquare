package com.planitsquare.holidaykeeper.holiday.entity;

import com.planitsquare.holidaykeeper.country.entity.Country;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "holidays")
@Getter
@Setter
@NoArgsConstructor
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", nullable = false)
    private Country country;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer holidayYear;

    private String localName;

    private String englishName;

    private boolean fixedHoliday;

    private boolean globalHoliday;

    private Integer launchYear;

    private String types;
}