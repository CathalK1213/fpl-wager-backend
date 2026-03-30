package com.fplwager.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FplEventDto {
    private Integer id;

    @JsonProperty("is_current")
    private Boolean isCurrent;
}