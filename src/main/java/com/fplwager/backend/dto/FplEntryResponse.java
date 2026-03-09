package com.fplwager.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FplEntryResponse {
    private Long id;
    private String name;

    @JsonProperty("player_first_name")
    private String playerFirstName;

    @JsonProperty("player_last_name")
    private String playerLastName;

    @JsonProperty("summary_overall_points")
    private Integer summaryOverallPoints;

    @JsonProperty("summary_overall_rank")
    private Integer summaryOverallRank;

    @JsonProperty("summary_event_points")
    private Integer summaryEventPoints;

    @JsonProperty("current_event")
    private Integer currentEvent;
}