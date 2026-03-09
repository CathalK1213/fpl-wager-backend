package com.fplwager.backend.service;

import com.fplwager.backend.dto.ProposeWagerRequest;
import com.fplwager.backend.dto.WagerResponse;
import com.fplwager.backend.model.*;
import com.fplwager.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WagerService {

    private final WagerRepository wagerRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public WagerResponse proposeWager(String username, ProposeWagerRequest request) {
        User proposer = getUser(username);
        User opponent = userRepository.findById(request.getOpponentId())
                .orElseThrow(() -> new IllegalArgumentException("Opponent not found"));
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (proposer.getId().equals(opponent.getId())) {
            throw new IllegalArgumentException("You cannot wager against yourself");
        }

        if (!groupMemberRepository.existsByGroupAndUser(group, proposer)) {
            throw new IllegalArgumentException("You are not a member of this group");
        }

        if (!groupMemberRepository.existsByGroupAndUser(group, opponent)) {
            throw new IllegalArgumentException("Opponent is not a member of this group");
        }

        Wager wager = Wager.builder()
                .proposer(proposer)
                .opponent(opponent)
                .group(group)
                .status(WagerStatus.PROPOSED)
                .wagerType(request.getWagerType())
                .stakeType(request.getStakeType())
                .stakeAmount(request.getStakeAmount())
                .stakeDescription(request.getStakeDescription())
                .description(request.getDescription())
                .gameweek(request.getGameweek())
                .build();

        return toWagerResponse(wagerRepository.save(wager));
    }

    @Transactional
    public WagerResponse respondToWager(String username, Long wagerId, String action) {
        User user = getUser(username);
        Wager wager = wagerRepository.findById(wagerId)
                .orElseThrow(() -> new IllegalArgumentException("Wager not found"));

        if (!wager.getOpponent().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not the opponent on this wager");
        }

        if (wager.getStatus() != WagerStatus.PROPOSED && wager.getStatus() != WagerStatus.COUNTER_OFFERED) {
            throw new IllegalArgumentException("Wager cannot be responded to in its current state");
        }

        switch (action.toUpperCase()) {
            case "ACCEPT" -> wager.setStatus(WagerStatus.ACCEPTED);
            case "DECLINE" -> wager.setStatus(WagerStatus.DECLINED);
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        }

        return toWagerResponse(wagerRepository.save(wager));
    }

    public List<WagerResponse> getGroupWagers(String username, Long groupId) {
        User user = getUser(username);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("You are not a member of this group");
        }

        return wagerRepository.findByGroup(group).stream()
                .map(this::toWagerResponse)
                .toList();
    }

    public List<WagerResponse> getMyWagers(String username) {
        User user = getUser(username);
        return wagerRepository.findByProposerOrOpponent(user, user).stream()
                .map(this::toWagerResponse)
                .toList();
    }

    private WagerResponse toWagerResponse(Wager wager) {
        return WagerResponse.builder()
                .id(wager.getId())
                .proposerUsername(wager.getProposer().getUsername())
                .opponentUsername(wager.getOpponent().getUsername())
                .groupName(wager.getGroup().getName())
                .status(wager.getStatus())
                .wagerType(wager.getWagerType())
                .stakeType(wager.getStakeType())
                .stakeAmount(wager.getStakeAmount())
                .stakeDescription(wager.getStakeDescription())
                .description(wager.getDescription())
                .gameweek(wager.getGameweek())
                .winnerUsername(wager.getWinner() != null ? wager.getWinner().getUsername() : null)
                .counterOfferCount(wager.getCounterOfferCount())
                .createdAt(wager.getCreatedAt())
                .updatedAt(wager.getUpdatedAt())
                .build();
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}