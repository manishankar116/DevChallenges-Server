package com.app.devchallenges.Controller;

import com.app.devchallenges.Security.AuthenticatedUser;
import com.app.devchallenges.Service.ChallengeViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeViewController {

    private final ChallengeViewService challengeViewService;

    @PostMapping("/{challengeId}/view")
    public ResponseEntity<?> recordView(
            @PathVariable UUID challengeId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {

        UUID userId = authenticatedUser.getUserId();

        boolean recorded = challengeViewService.recordView(
                challengeId,
                userId
        );

        return ResponseEntity.ok(
                Map.of(
                        "recorded", recorded
                )
        );
    }

    @GetMapping("/views/user")
    public ResponseEntity<?> getTotalViews(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {

        long totalViews = challengeViewService.getTotalViewsForUser(
                authenticatedUser.getUserId()
        );

        return ResponseEntity.ok(
                Map.of("totalViews", totalViews)
        );
    }
}