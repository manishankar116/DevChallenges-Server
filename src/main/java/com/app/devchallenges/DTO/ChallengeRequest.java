package com.app.devchallenges.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ChallengeRequest(
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
