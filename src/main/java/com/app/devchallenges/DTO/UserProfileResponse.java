package com.app.devchallenges.DTO;

import java.util.UUID;

public record UserProfileResponse(
        String fullname,
        String username,
        String email,
        UUID userProfileID,
        String headline,
        String bio,
        String location,
        String currentCompany,
        String currentRole,
        String website,
        String profileImageUrl,
        boolean isOwner
) {}
