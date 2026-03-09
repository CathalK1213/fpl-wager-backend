package com.fplwager.backend.dto;

import com.fplwager.backend.model.StakeType;
import com.fplwager.backend.model.WagerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProposeWagerRequest {
    @NotNull
    private Long opponentId;

    @NotNull
    private Long groupId;

    @NotBlank
    private String description;

    @NotNull
    private StakeType stakeType;

    @NotNull
    private WagerType wagerType;

    private BigDecimal stakeAmount;
    private String stakeDescription;
    private Integer gameweek;
}