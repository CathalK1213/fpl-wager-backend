package com.fplwager.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class FplBootstrapResponse {
    private List<FplEventDto> events;
}