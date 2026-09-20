package com.app.devchallenges.Service;

import com.app.devchallenges.DTO.ChallengeRequest;
import com.app.devchallenges.DTO.ChallengeResponse;
import com.app.devchallenges.Entity.Challenge;
import com.app.devchallenges.Entity.User;
import com.app.devchallenges.Repository.ChallengeRepository;
import com.app.devchallenges.Repository.UserRepository;
import com.app.devchallenges.Security.AuthenticatedUser;
import jakarta.transaction.Transactional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    public ChallengeService(ChallengeRepository challengeRepository, UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ChallengeResponse createChallenge(ChallengeRequest request, AuthenticatedUser authenticatedUser){
        User user = userRepository.findByUserId(authenticatedUser.getUserId()).orElseThrow(() ->
                new RuntimeException(
                        "User not found: " + authenticatedUser.getUserId()
                )
        );

        Challenge challenge = new Challenge();
        challenge.setTitle(request.title());
        challenge.setDescription(request.description());
        challenge.setActualResult(request.actualResult());
        challenge.setExpectedResult(request.expectedResult());
        challenge.setEnvironment(request.environment());
        challenge.setContribution(request.contribution());
        challenge.setAttemptsXML(request.attemptsXML());
        challenge.setLearnings(request.learnings());
        challenge.setViews(0L);

        challenge.setUser(user);

        Instant now = Instant.now();
        challenge.setCreatedAt(now);
        challenge.setUpdatedAt(now);

        Challenge savedChallenge = challengeRepository.save(challenge);

        return ChallengeResponse.from(savedChallenge);

    }

    public ChallengeResponse getChallengeById(UUID id) {

        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found with ID: " + id));

        return ChallengeResponse.from(challenge);
    }

    public List<ChallengeResponse> getChallengesByUsername(String username){
         return challengeRepository.findByUserUsername(username)
                 .stream()
                 .sorted(Comparator.comparing(Challenge::getCreatedAt))
                 .map(ChallengeResponse::from)
                 .toList();
    }


    public boolean deleteChallengeById(UUID id) {
        try {
            challengeRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public ChallengeResponse updateChallenge(UUID id, ChallengeRequest request) {
            // 1. Fetch existing challenge using the ID from the URL path
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Challenge not found with id: " + id));

        // 2. Map incoming partial updates (PATCH) using record accessor syntax
        if (request.title() != null) challenge.setTitle(request.title());
        if (request.description() != null) challenge.setDescription(request.description());
        if (request.actualResult() != null) challenge.setActualResult(request.actualResult());
        if (request.expectedResult() != null) challenge.setExpectedResult(request.expectedResult());
        if (request.environment() != null) challenge.setEnvironment(request.environment());
        if (request.contribution() != null) challenge.setContribution(request.contribution());
        if (request.attemptsXML() != null) challenge.setAttemptsXML(request.attemptsXML());
        if (request.learnings() != null) challenge.setLearnings(request.learnings());

        challenge.setUpdatedAt(Instant.now());

        // 3. Persist changes to the database
        Challenge updatedChallenge = challengeRepository.save(challenge);

        // 4. Return the data using a traditional constructor call (No Builder)
        return ChallengeResponse.from(updatedChallenge);

    }



}
