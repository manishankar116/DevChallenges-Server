package com.app.devchallenges.Repository;

import com.app.devchallenges.DTO.ChallengeResponse;
import com.app.devchallenges.Entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    List<Challenge> findByUserUsername(String username);

    @Modifying
    @Query("""
        UPDATE Challenge c
        SET c.views = c.views + 1
        WHERE c.challengeId = :challengeId
    """)
    int incrementViews(UUID challengeId);
}
