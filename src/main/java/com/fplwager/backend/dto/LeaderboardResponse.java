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
public class LeaderboardResponse {
    private Long groupId;
    private String groupName;
    private Integer currentGameweek;
    private List<LeaderboardEntry> standings;
}