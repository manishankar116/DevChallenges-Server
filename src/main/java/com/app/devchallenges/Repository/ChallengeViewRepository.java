package com.app.devchallenges.Repository;

import com.app.devchallenges.Entity.ChallengeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChallengeViewRepository
        extends JpaRepository<ChallengeView, UUID> {

    @Modifying
    @Query(
            value = """
            INSERT INTO challenge_views
                (view_id, challenge_id, user_id, viewed_at)
            VALUES
                (gen_random_uuid(), :challengeId, :userId, CURRENT_TIMESTAMP)
            ON CONFLICT (challenge_id, user_id)
            DO NOTHING
            """,
            nativeQuery = true
    )
    int recordView(
            @Param("challengeId") UUID challengeId,
            @Param("userId") UUID userId
    );
}
