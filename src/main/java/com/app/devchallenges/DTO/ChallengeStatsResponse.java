package com.app.devchallenges.DTO;

public record ChallengeStatsResponse(
        long challengeCount,
        long totalViews,
        long uniqueTagCount
) {
}
