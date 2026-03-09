package com.fplwager.backend.repository;

import com.fplwager.backend.model.Group;
import com.fplwager.backend.model.TrashTalkMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrashTalkRepository extends JpaRepository<TrashTalkMessage, Long> {
    List<TrashTalkMessage> findByGroupAndGameweekOrderBySentAtAsc(Group group, Integer gameweek);
    List<TrashTalkMessage> findByGroupOrderBySentAtDesc(Group group);
}