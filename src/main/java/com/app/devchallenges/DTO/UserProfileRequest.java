package com.app.devchallenges.DTO;

import org.springframework.web.multipart.MultipartFile;

public record UserProfileRequest(
        String fullname,
        String headline,
        String bio,
        String location,
        String currentCompany,
        String currentRole,
        String website,
        MultipartFile profileImage
) {
}
