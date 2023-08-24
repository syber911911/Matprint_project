package com.example.final_project_17team.named.dto;

import com.example.final_project_17team.restaurant.entity.Restaurant;
import lombok.Data;

@Data
public class NamedPageDto {

    private String title;
    private String address;

    public static NamedPageDto fromEntity(Restaurant restaurant){
        NamedPageDto dto = new NamedPageDto();

        dto.setTitle(restaurant.getName());
        dto.setAddress(restaurant.getRoadAddress());
        if(dto.address == null){
            dto.setAddress(restaurant.getAddress());
        }
        return dto;
    }

}
