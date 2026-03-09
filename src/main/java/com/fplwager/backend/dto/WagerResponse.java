package com.fplwager.backend.dto;

import com.fplwager.backend.model.StakeType;
import com.fplwager.backend.model.WagerStatus;
import com.fplwager.backend.model.WagerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WagerResponse {
    private Long id;
    private String proposerUsername;
    private String opponentUsername;
    private String groupName;
    private WagerStatus status;
    private WagerType wagerType;
    private StakeType stakeType;
    private BigDecimal stakeAmount;
    private String stakeDescription;
    private String description;
    private Integer gameweek;
    private String winnerUsername;
    private Integer counterOfferCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}