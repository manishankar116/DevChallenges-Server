package com.app.devchallenges.DTO;

import com.app.devchallenges.Entity.Challenge;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChallengeResponse(
        UUID challengeId,
        String title,
        String description,
        String actualResult,
        String expectedResult,
        List<String> environment,
        String contribution,
        String attemptsXML,
        String learnings,
        Instant createdAt,
        Instant updatedAt,
        UUID userId,
        Long views
) {
    public static ChallengeResponse from(Challenge challenge) {
        return new ChallengeResponse(
                challenge.getChallengeId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getActualResult(),
                challenge.getExpectedResult(),
                challenge.getEnvironment(),
                challenge.getContribution(),
                challenge.getAttemptsXML(),
                challenge.getLearnings(),
                challenge.getCreatedAt(),
                challenge.getUpdatedAt(),
                challenge.getUser() != null ? challenge.getUser().getUserId() : null,
                challenge.getViews()
        );
    }
}
