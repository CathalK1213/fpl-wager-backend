package com.fplwager.backend.repository;

import com.fplwager.backend.model.FplScore;
import com.fplwager.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FplScoreRepository extends JpaRepository<FplScore, Long> {
    List<FplScore> findByUserOrderByGameweekDesc(User user);
    Optional<FplScore> findByUserAndGameweek(User user, Integer gameweek);
    List<FplScore> findByGameweek(Integer gameweek);
}