package com.fplwager.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonWrappedResponse {
    private String username;
    private String fplTeamName;

    // FPL Stats
    private Integer totalPoints;
    private Integer overallRank;
    private Integer bestGameweekPoints;
    private Integer bestGameweek;
    private Integer currentGameweek;

    // Wager Stats
    private Integer totalWagers;
    private Integer wagersWon;
    private Integer wagersLost;
    private Integer wagerWinRate;
    private String biggestWinDescription;
    private String worstLossDescription;

    // Group Stats
    private Integer groupPosition;
    private String groupName;
    private Integer totalGroupMembers;
}