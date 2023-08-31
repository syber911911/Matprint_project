package com.example.final_project_17team.dataUpdate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class PlaceDataDto {
    private String name;
    private String tel;
    private List<String> category;
    private String openHours;
    private String closeHours;
    private String address;
    private String roadAddress;
    private List<String> shortAddress;
    private List<String> thumUrls;
    private BigDecimal x;
    private BigDecimal y;
    private String menuInfo;

    public PlaceDataDto() {
    }
}