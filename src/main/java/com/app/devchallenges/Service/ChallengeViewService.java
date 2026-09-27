package com.app.devchallenges.Service;

import com.app.devchallenges.Entity.ChallengeView;
import com.app.devchallenges.Repository.ChallengeViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeViewService {

    private final ChallengeViewRepository challengeViewRepository;

    @Transactional
    public boolean recordView(UUID challengeId, UUID userId) {

        // Already viewed by this user
        if (challengeViewRepository.existsByChallengeIdAndUserId(
                challengeId,
                userId
        )) {
            return false;
        }

        ChallengeView challengeView = ChallengeView.builder()
                .challengeId(challengeId)
                .userId(userId)
                .viewedAt(LocalDateTime.now())
                .build();

        challengeViewRepository.save(challengeView);

        return true;
    }

    public long getViewCount(UUID challengeId) {
        return challengeViewRepository.countByChallengeId(challengeId);
    }

    public long getTotalViewsForUser(UUID userId) {
        return challengeViewRepository.countViewsReceivedByUser(userId);
    }


}