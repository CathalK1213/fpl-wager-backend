package com.fplwager.backend.service;

import com.fplwager.backend.dto.LeaderboardEntry;
import com.fplwager.backend.dto.LeaderboardResponse;
import com.fplwager.backend.dto.FplEntryResponse;
import com.fplwager.backend.model.Group;
import com.fplwager.backend.model.GroupMember;
import com.fplwager.backend.model.User;
import com.fplwager.backend.repository.GroupMemberRepository;
import com.fplwager.backend.repository.GroupRepository;
import com.fplwager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final FplSyncService fplSyncService;

    public LeaderboardResponse getLeaderboard(Long groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        User requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!groupMemberRepository.existsByGroupAndUser(group, requestingUser)) {
            throw new IllegalArgumentException("You are not a member of this group");
        }

        List<GroupMember> members = groupMemberRepository.findByGroup(group);
        List<LeaderboardEntry> entries = new ArrayList<>();
        Integer currentGameweek = null;

        for (GroupMember member : members) {
            User user = member.getUser();
            LeaderboardEntry.LeaderboardEntryBuilder entryBuilder = LeaderboardEntry.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .fplTeamId(user.getFplTeamId());

            if (user.getFplTeamId() != null) {
                FplEntryResponse fplEntry = fplSyncService.syncUser(user);
                if (fplEntry != null) {
                    entryBuilder
                            .fplTeamName(fplEntry.getName())
                            .gameweekPoints(fplEntry.getSummaryEventPoints())
                            .totalPoints(fplEntry.getSummaryOverallPoints())
                            .overallRank(fplEntry.getSummaryOverallRank());

                    if (currentGameweek == null) {
                        currentGameweek = fplEntry.getCurrentEvent();
                    }
                }
            } else {
                entryBuilder
                        .fplTeamName("Not linked")
                        .gameweekPoints(0)
                        .totalPoints(0);
            }

            entries.add(entryBuilder.build());
        }

        entries.sort(Comparator.comparingInt(
                e -> -(e.getTotalPoints() != null ? e.getTotalPoints() : 0)));

        AtomicInteger position = new AtomicInteger(1);
        entries.forEach(e -> e.setPosition(position.getAndIncrement()));

        return LeaderboardResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .currentGameweek(currentGameweek)
                .standings(entries)
                .build();
    }
}