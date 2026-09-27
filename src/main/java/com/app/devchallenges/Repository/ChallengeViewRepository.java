package com.app.devchallenges.Repository;

import com.app.devchallenges.Entity.ChallengeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChallengeViewRepository
        extends JpaRepository<ChallengeView, UUID> {

    boolean existsByChallengeIdAndUserId(UUID challengeId, UUID userId);

    long countByChallengeId(UUID challengeId);

    @Query("""
    SELECT COUNT(cv)
    FROM ChallengeView cv
    JOIN Challenge c ON c.challengeId = cv.challengeId
    WHERE c.user.userId = :userId
    """)
    long countViewsReceivedByUser(@Param("userId") UUID userId);
}
