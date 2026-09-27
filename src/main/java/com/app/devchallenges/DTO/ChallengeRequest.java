package com.app.devchallenges.DTO;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record ChallengeRequest(

        UUID challengeId,

        @NotBlank(message = "Title is required")
        String title,

        String description,

        String actualResult,
        String expectedResult,
        List<String> environment,

        String contribution, // Personal Project, Team Project, Client Project, Open Source

        String attemptsXML,
        String learnings
) {

}
