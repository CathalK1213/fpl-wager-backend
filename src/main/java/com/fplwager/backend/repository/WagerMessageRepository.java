package com.fplwager.backend.repository;

import com.fplwager.backend.model.Wager;
import com.fplwager.backend.model.WagerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WagerMessageRepository extends JpaRepository<WagerMessage, Long> {
    List<WagerMessage> findByWagerOrderBySentAtAsc(Wager wager);
}