package com.planitsquare.holidaykeeper.dateapi.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class NagerHolidayDto {
    private String date;
    private String localName;
    private String name;
    private String countryCode;
    private boolean fixed;
    private boolean global;
    private Integer launchYear;
    private List<String> types;
}
