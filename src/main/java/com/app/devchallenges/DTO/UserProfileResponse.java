package com.app.devchallenges.DTO;

import java.util.UUID;

public record UserProfileResponse(
        String fullname,
        String username,
        UUID userProfileID,
        String headline,
        String bio,
        String location,
        String currentCompany,
        String currentRole,
        String website,
        boolean isOwner
) {}
