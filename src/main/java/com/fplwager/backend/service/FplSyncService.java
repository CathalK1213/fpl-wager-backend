package com.fplwager.backend.service;

import com.fplwager.backend.client.FplApiClient;
import com.fplwager.backend.dto.FplEntryResponse;
import com.fplwager.backend.model.FplScore;
import com.fplwager.backend.model.User;
import com.fplwager.backend.repository.FplScoreRepository;
import com.fplwager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FplSyncService {

    private final FplApiClient fplApiClient;
    private final UserRepository userRepository;
    private final FplScoreRepository fplScoreRepository;

    @Scheduled(fixedDelay = 300000) // every 5 minutes
    public void syncAllTeams() {
        List<User> usersWithTeams = userRepository.findAll().stream()
                .filter(u -> u.getFplTeamId() != null)
                .toList();

        if (usersWithTeams.isEmpty()) {
            log.debug("No users with FPL teams to sync");
            return;
        }

        log.info("Syncing FPL scores for {} users", usersWithTeams.size());
        usersWithTeams.forEach(this::syncUser);
    }

    @Transactional
    public FplEntryResponse syncUser(User user) {
        if (user.getFplTeamId() == null) return null;

        FplEntryResponse entry = fplApiClient.getEntry(user.getFplTeamId());
        if (entry == null || entry.getCurrentEvent() == null) return null;

        Optional<FplScore> existing = fplScoreRepository
                .findByUserAndGameweek(user, entry.getCurrentEvent());

        FplScore score = existing.orElse(FplScore.builder()
                .user(user)
                .gameweek(entry.getCurrentEvent())
                .build());

        score.setPoints(entry.getSummaryEventPoints() != null ? entry.getSummaryEventPoints() : 0);
        score.setRank(entry.getSummaryOverallRank());
        score.setTotalPoints(entry.getSummaryOverallPoints());

        fplScoreRepository.save(score);
        log.info("Synced FPL score for user {} - GW{}: {} pts",
                user.getUsername(), entry.getCurrentEvent(), score.getPoints());

        return entry;
    }
}