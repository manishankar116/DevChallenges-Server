package com.app.devchallenges.Controller;

import com.app.devchallenges.DTO.UserProfileRequest;
import com.app.devchallenges.DTO.UserProfileResponse;
import com.app.devchallenges.Entity.User;
import com.app.devchallenges.Security.AuthenticatedUser;
import com.app.devchallenges.Service.UserProfileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }

    @GetMapping("/{profileId}")
    public UserProfileResponse getUserProfileById(@PathVariable UUID profileId,
                                                  @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        UserProfileResponse userProfileResponse = userProfileService.getUserProfileById(profileId, authenticatedUser.getUserId());

        return userProfileResponse;
    }

    @GetMapping("/profile-data")
    @PreAuthorize("isAuthenticated()")
    public UserProfileResponse getUserProfileByUserId(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        return userProfileService.getUserProfileByUserId(authenticatedUser.getUserId());
    }

    @GetMapping("/profile-data/{username}")
    public UserProfileResponse getProfileByUsername(@PathVariable String username){
        return userProfileService.getProfileByUsername(username);
    }

    @PatchMapping("/update/{profileId}")
    public UserProfileResponse updateUserProfileById(@PathVariable UUID profileId, @RequestBody UserProfileRequest userProfileRequest){
        return userProfileService.updateUserProfileById(profileId, userProfileRequest);
    }

    @PostMapping("/create-profile")
    public boolean createUserProfile(User user) {
        return userProfileService.createUserProfile(user);
    }

}
