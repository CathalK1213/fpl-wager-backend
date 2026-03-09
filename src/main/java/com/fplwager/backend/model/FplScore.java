package com.fplwager.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fpl_scores",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "gameweek"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FplScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer gameweek;

    @Column(nullable = false)
    private Integer points;

    @Column
    private Integer rank;

    @Column
    private Integer totalPoints;

    @Column
    private LocalDateTime syncedAt;

    @PrePersist
    @PreUpdate
    protected void onSync() {
        syncedAt = LocalDateTime.now();
    }
}
