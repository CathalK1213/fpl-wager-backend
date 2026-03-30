package com.fplwager.backend.client;

import com.fplwager.backend.dto.FplBootstrapResponse;
import com.fplwager.backend.dto.FplEntryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class FplApiClient {

    private static final String FPL_BASE_URL = "https://fantasy.premierleague.com/api";

    private final RestClient restClient = RestClient.builder()
            .baseUrl(FPL_BASE_URL)
            .defaultHeader("User-Agent", "fpl-wager-app")
            .build();

    public FplEntryResponse getEntry(Long teamId) {
        try {
            return restClient.get()
                    .uri("/entry/{teamId}/", teamId)
                    .retrieve()
                    .body(FplEntryResponse.class);
        } catch (Exception e) {
            log.error("Failed to fetch FPL entry for team {}: {}", teamId, e.getMessage());
            return null;
        }
    }

    public FplBootstrapResponse getBootstrapStatic() {
        try {
            return restClient.get()
                    .uri("/bootstrap-static/")
                    .retrieve()
                    .body(FplBootstrapResponse.class);
        } catch (Exception e) {
            log.error("Failed to fetch FPL bootstrap: {}", e.getMessage());
            return null;
        }
    }
}