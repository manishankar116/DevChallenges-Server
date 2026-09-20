package com.app.devchallenges.DTO;

public record UserProfileRequest(
        String headline,
        String bio,
        String location,
        String currentCompany,
        String currentRole,
        String website
) {
}
