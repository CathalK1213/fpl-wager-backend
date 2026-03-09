package com.fplwager.backend.repository;

import com.fplwager.backend.model.Group;
import com.fplwager.backend.model.User;
import com.fplwager.backend.model.Wager;
import com.fplwager.backend.model.WagerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WagerRepository extends JpaRepository<Wager, Long> {
    List<Wager> findByGroup(Group group);
    List<Wager> findByProposerOrOpponent(User proposer, User opponent);
    List<Wager> findByGroupAndStatus(Group group, WagerStatus status);

    @Query("SELECT w FROM Wager w WHERE w.group = :group AND (w.proposer = :user OR w.opponent = :user)")
    List<Wager> findByGroupAndUser(Group group, User user);
}