package com.fplwager.backend.service;

import com.fplwager.backend.client.FplApiClient;
import com.fplwager.backend.dto.FplEntryResponse;
import com.fplwager.backend.dto.SeasonWrappedResponse;
import com.fplwager.backend.model.*;
import com.fplwager.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeasonWrappedService {

    private final UserRepository userRepository;
    private final FplScoreRepository fplScoreRepository;
    private final WagerRepository wagerRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final FplApiClient fplApiClient;

    public SeasonWrappedResponse getWrapped(String username, Long groupId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("You are not a member of this group");
        }

        // FPL Stats
        FplEntryResponse fplEntry = user.getFplTeamId() != null
                ? fplApiClient.getEntry(user.getFplTeamId()) : null;

        List<FplScore> scores = fplScoreRepository.findByUserOrderByGameweekDesc(user);

        FplScore bestScore = scores.stream()
                .max(Comparator.comparingInt(FplScore::getPoints))
                .orElse(null);

        // Wager Stats
        List<Wager> allWagers = wagerRepository.findByGroupAndUser(group, user).stream()
                .filter(w -> w.getStatus() == WagerStatus.COMPLETED ||
                        w.getStatus() == WagerStatus.SETTLED)
                .toList();

        int won = (int) allWagers.stream()
                .filter(w -> w.getWinner() != null && w.getWinner().getId().equals(user.getId()))
                .count();
        int lost = allWagers.size() - won;
        int winRate = allWagers.isEmpty() ? 0 : (int) ((won * 100.0) / allWagers.size());

        Wager biggestWin = allWagers.stream()
                .filter(w -> w.getWinner() != null && w.getWinner().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        Wager worstLoss = allWagers.stream()
                .filter(w -> w.getWinner() != null && !w.getWinner().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        // Group position
        List<GroupMember> members = groupMemberRepository.findByGroup(group);
        List<User> rankedUsers = members.stream()
                .map(GroupMember::getUser)
                .filter(u -> u.getFplTeamId() != null)
                .sorted((a, b) -> {
                    List<FplScore> aScores = fplScoreRepository.findByUserOrderByGameweekDesc(a);
                    List<FplScore> bScores = fplScoreRepository.findByUserOrderByGameweekDesc(b);
                    int aTotal = aScores.stream().mapToInt(FplScore::getPoints).sum();
                    int bTotal = bScores.stream().mapToInt(FplScore::getPoints).sum();
                    return Integer.compare(bTotal, aTotal);
                })
                .toList();

        int position = rankedUsers.indexOf(user) + 1;

        return SeasonWrappedResponse.builder()
                .username(username)
                .fplTeamName(fplEntry != null ? fplEntry.getName() : "Not linked")
                .totalPoints(fplEntry != null ? fplEntry.getSummaryOverallPoints() : 0)
                .overallRank(fplEntry != null ? fplEntry.getSummaryOverallRank() : null)
                .bestGameweekPoints(bestScore != null ? bestScore.getPoints() : 0)
                .bestGameweek(bestScore != null ? bestScore.getGameweek() : null)
                .currentGameweek(fplEntry != null ? fplEntry.getCurrentEvent() : null)
                .totalWagers(allWagers.size())
                .wagersWon(won)
                .wagersLost(lost)
                .wagerWinRate(winRate)
                .biggestWinDescription(biggestWin != null ? biggestWin.getDescription() : null)
                .worstLossDescription(worstLoss != null ? worstLoss.getDescription() : null)
                .groupPosition(position > 0 ? position : null)
                .groupName(group.getName())
                .totalGroupMembers(members.size())
                .build();
    }
}