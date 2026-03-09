package com.fplwager.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtSummary {
    private String username;
    private List<DebtEntry> owes;
    private List<DebtEntry> owedBy;
    private Integer totalWagersWon;
    private Integer totalWagersLost;
}