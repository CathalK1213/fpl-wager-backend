package com.fplwager.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {
    private Long userId;
    private String username;
    private String fplTeamName;
    private Integer gameweekPoints;
    private Integer totalPoints;
    private Integer overallRank;
    private Integer position;
    private Long fplTeamId;
}
