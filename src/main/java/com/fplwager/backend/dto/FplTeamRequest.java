package com.fplwager.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FplTeamRequest {
    @NotNull
    private Long fplTeamId;
}