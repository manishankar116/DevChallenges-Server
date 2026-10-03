package com.app.devchallenges.Controller;

import com.app.devchallenges.DTO.ChallengeRequest;
import com.app.devchallenges.DTO.ChallengeResponse;
import com.app.devchallenges.DTO.ChallengeStatsResponse;
import com.app.devchallenges.Security.AuthenticatedUser;
import com.app.devchallenges.Security.JwtService;
import com.app.devchallenges.Service.ChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@CrossOrigin(origins = "https://devchallenges.in")
@RestController
@RequestMapping("/api/challenge")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final JwtService jwtService;

    public ChallengeController(ChallengeService challengeService, JwtService jwtService) {
        this.challengeService = challengeService;
        this.jwtService = jwtService;
    }


    @PostMapping("/create-challenge")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChallengeResponse> createChallenge(@RequestBody  ChallengeRequest request,
                                                             @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        System.out.println(request.attemptsXML());
        return ResponseEntity.ok(challengeService.createChallenge(request, authenticatedUser));
    }

    @GetMapping("/get-challenge/{id}")
    public ResponseEntity<ChallengeResponse> getChallengeById(@PathVariable UUID id,
                                                              @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        return ResponseEntity.ok(challengeService.getChallengeById(id, authenticatedUser));
    }

    @GetMapping("/get-challenges-by-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChallengeResponse>> getChallengesById(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        return ResponseEntity.ok(challengeService.getChallengesByUsername(authenticatedUser.getUsername()));
    }

    @GetMapping("/get-challenges-by-user/{username}")
    public ResponseEntity<List<ChallengeResponse>> getChallengesByUsername(@PathVariable String username){
        return ResponseEntity.ok(challengeService.getChallengesByUsername(username));
    }

    @GetMapping("/get-user-stats/{username}")
    public ResponseEntity<ChallengeStatsResponse> getChallengeStatsByUsername(@PathVariable String username,
                                                                              @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(challengeService.getChallengeStatsByUsername(username, authenticatedUser));
    }

    @PatchMapping("/update-challenge")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChallengeResponse> updateChallenge(@RequestBody ChallengeRequest request,
                                                             @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        ChallengeResponse response = challengeService.updateChallenge(request, authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-challenge/{id}")
    @PreAuthorize("isAuthenticated()")
    public boolean deleteChallengeById(@PathVariable UUID id){
        return challengeService.deleteChallengeById(id);
    }

}
