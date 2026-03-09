package com.fplwager.backend.dto;

import com.fplwager.backend.model.StakeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtEntry {
    private String counterpartyUsername;
    private StakeType stakeType;
    private BigDecimal stakeAmount;
    private String stakeDescription;
    private Long wagerId;
}