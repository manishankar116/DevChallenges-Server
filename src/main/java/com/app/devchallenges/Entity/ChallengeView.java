package com.app.devchallenges.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "challenge_views",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_challenge_user_view",
                        columnNames = {"challenge_id", "user_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "view_id")
    private UUID viewId;

    @Column(name = "challenge_id", nullable = false)
    private UUID challengeId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;
}
