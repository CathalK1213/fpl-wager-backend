package com.fplwager.backend.service;

import com.fplwager.backend.dto.DebtEntry;
import com.fplwager.backend.dto.DebtSummary;
import com.fplwager.backend.model.*;
import com.fplwager.backend.repository.GroupMemberRepository;
import com.fplwager.backend.repository.GroupRepository;
import com.fplwager.backend.repository.UserRepository;
import com.fplwager.backend.repository.WagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final WagerRepository wagerRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public DebtSummary getMyDebtSummary(String username, Long groupId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("You are not a member of this group");
        }

        List<Wager> allWagers = wagerRepository.findByGroupAndUser(group, user);

        List<DebtEntry> owes = new ArrayList<>();
        List<DebtEntry> owedBy = new ArrayList<>();
        int won = 0;
        int lost = 0;

        for (Wager wager : allWagers) {
            if (wager.getStatus() != WagerStatus.COMPLETED &&
                    wager.getStatus() != WagerStatus.SETTLED) continue;

            if (wager.getWinner() == null) continue;

            boolean userWon = wager.getWinner().getId().equals(user.getId());

            if (userWon) {
                won++;
                String debtor = wager.getProposer().getId().equals(user.getId())
                        ? wager.getOpponent().getUsername()
                        : wager.getProposer().getUsername();

                owedBy.add(DebtEntry.builder()
                        .counterpartyUsername(debtor)
                        .stakeType(wager.getStakeType())
                        .stakeAmount(wager.getStakeAmount())
                        .stakeDescription(wager.getStakeDescription())
                        .wagerId(wager.getId())
                        .build());
            } else {
                lost++;
                String creditor = wager.getWinner().getUsername();

                owes.add(DebtEntry.builder()
                        .counterpartyUsername(creditor)
                        .stakeType(wager.getStakeType())
                        .stakeAmount(wager.getStakeAmount())
                        .stakeDescription(wager.getStakeDescription())
                        .wagerId(wager.getId())
                        .build());
            }
        }

        return DebtSummary.builder()
                .username(username)
                .owes(owes)
                .owedBy(owedBy)
                .totalWagersWon(won)
                .totalWagersLost(lost)
                .build();
    }
}