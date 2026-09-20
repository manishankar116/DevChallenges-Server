package com.app.devchallenges.Service;

import com.app.devchallenges.DTO.UserProfileRequest;
import com.app.devchallenges.DTO.UserProfileResponse;
import com.app.devchallenges.Entity.User;
import com.app.devchallenges.Entity.UserProfile;
import com.app.devchallenges.Repository.UserProfileRepository;
import com.app.devchallenges.Repository.UserRepository;
import org.hibernate.annotations.NotFound;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileService(UserProfileRepository userProfileRepository, UserRepository userRepository){
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    public UserProfileResponse updateUserProfileById(UUID profileId, UserProfileRequest userProfileDTO) {
        UserProfile existingProfile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if(userProfileDTO.headline() != null){
            existingProfile.setHeadline(userProfileDTO.headline());
        }
        if (userProfileDTO.bio() != null){
            existingProfile.setBio(userProfileDTO.bio());
        }
        if (userProfileDTO.location() != null) {
            existingProfile.setLocation(userProfileDTO.location());
        }
        if (userProfileDTO.currentCompany() != null) {
            existingProfile.setCurrentCompany(userProfileDTO.currentCompany());
        }
        if (userProfileDTO.currentRole() != null) {
            existingProfile.setCurrentRole(userProfileDTO.currentRole());
        }
        if (userProfileDTO.website() != null) {
            existingProfile.setWebsite(userProfileDTO.website());
        }

        UserProfile updatedProfile = userProfileRepository.save(existingProfile);

        return new UserProfileResponse(
                "fullname(restricted)",
                "username(restricted)",
                updatedProfile.getProfileId(),
                updatedProfile.getHeadline(),
                updatedProfile.getBio(),
                updatedProfile.getLocation(),
                updatedProfile.getCurrentCompany(),
                updatedProfile.getCurrentRole(),
                updatedProfile.getWebsite(),
                true
        );
    }

    public UserProfileResponse getUserProfileById(UUID profileId, UUID userId){
        UserProfile userProfile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        User user = userRepository.findByUserId(userId).orElse(new User());
        return new UserProfileResponse(
                user.getFullName(),
                user.getUsername(),
                userProfile.getProfileId(),
                userProfile.getHeadline(),
                userProfile.getBio(),
                userProfile.getLocation(),
                userProfile.getCurrentCompany(),
                userProfile.getCurrentRole(),
                userProfile.getWebsite(),
                userId == user.getUserId()
        );
    }

    public UserProfileResponse getUserProfileByUserId(UUID userId){
        UserProfile userProfile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        User user = userRepository.findByUserId(userId).orElse(new User());

        return new UserProfileResponse(
                user.getFullName(),
                user.getUsername(),
                userProfile.getProfileId(),
                userProfile.getHeadline(),
                userProfile.getBio(),
                userProfile.getLocation(),
                userProfile.getCurrentCompany(),
                userProfile.getCurrentRole(),
                userProfile.getWebsite(),
                userId.equals(user.getUserId())
        );
    }

    public boolean createUserProfile(User user) {
        UserProfile profile = new UserProfile(user);
        UserProfile savedProfile = userProfileRepository.save(profile);
        return true;
    }

    public UserProfileResponse getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username " + username));
        UserProfile userProfile = userProfileRepository.findByUser_UserId(user.getUserId()).orElseThrow(() -> new IllegalArgumentException("profile not found"));

        return new UserProfileResponse(
                user.getFullName(),
                user.getUsername(),
                userProfile.getProfileId(),
                userProfile.getHeadline(),
                userProfile.getBio(),
                userProfile.getLocation(),
                userProfile.getCurrentCompany(),
                userProfile.getCurrentRole(),
                userProfile.getWebsite(),
                false
        );
    }
}