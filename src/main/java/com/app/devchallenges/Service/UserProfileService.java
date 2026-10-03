package com.app.devchallenges.Service;

import com.app.devchallenges.DTO.UserProfileRequest;
import com.app.devchallenges.DTO.UserProfileResponse;
import com.app.devchallenges.DTO.ProfileImageData;
import com.app.devchallenges.Entity.User;
import com.app.devchallenges.Entity.UserProfile;
import com.app.devchallenges.Repository.UserProfileRepository;
import com.app.devchallenges.Repository.UserRepository;
import com.app.devchallenges.Security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);
    private static final int MAX_PROFILE_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final NeonObjectStorageService neonObjectStorageService;

    public UserProfileService(
            UserProfileRepository userProfileRepository,
            UserRepository userRepository,
            NeonObjectStorageService neonObjectStorageService) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.neonObjectStorageService = neonObjectStorageService;
    }

    @Transactional
    public UserProfileResponse updateUserProfileById(UserProfileRequest userProfileDTO, AuthenticatedUser authenticatedUser) {
        MultipartFile profileImage = userProfileDTO.profileImage();
        ValidatedProfileImage validatedImage = profileImage == null
                ? null
                : validateProfileImage(profileImage);

        UserProfile existingProfile = userProfileRepository.findByUser_Username(authenticatedUser.getUsername())
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

        User user = existingProfile.getUser();
        boolean userChanged = false;
        if (userProfileDTO.fullname() != null) {
            user.setFullName(userProfileDTO.fullname());
            user.setUpdatedAt(Instant.now());
            userChanged = true;
        }
        if (validatedImage != null) {
            Optional<String> storedImageUrl;
            try {
                storedImageUrl = neonObjectStorageService.uploadProfileImage(
                        user.getUserId(),
                        validatedImage.data(),
                        validatedImage.contentType());
            } catch (SdkException exception) {
                logger.warn("Neon Object Storage upload failed; storing profile image in PostgreSQL instead",
                        exception);
                storedImageUrl = Optional.empty();
            } catch (IllegalArgumentException exception) {
                logger.warn("Neon Object Storage configuration is invalid; storing profile image in PostgreSQL instead",
                        exception);
                storedImageUrl = Optional.empty();
            }

            if (storedImageUrl.isPresent()) {
                user.setProfileImageUrl(storedImageUrl.get());
                user.setProfileImageData(null);
                user.setProfileImageContentType(null);
            } else {
                user.setProfileImageUrl(null);
                user.setProfileImageData(validatedImage.data());
                user.setProfileImageContentType(validatedImage.contentType());
            }
            user.setUpdatedAt(Instant.now());
            userChanged = true;
        }
        if (userChanged) {
            userRepository.save(user);
        }

        UserProfile updatedProfile = userProfileRepository.save(existingProfile);

        return new UserProfileResponse(
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                updatedProfile.getProfileId(),
                updatedProfile.getHeadline(),
                updatedProfile.getBio(),
                updatedProfile.getLocation(),
                updatedProfile.getCurrentCompany(),
                updatedProfile.getCurrentRole(),
                updatedProfile.getWebsite(),
                profileImageUrl(user),
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
                user.getEmail(),
                userProfile.getProfileId(),
                userProfile.getHeadline(),
                userProfile.getBio(),
                userProfile.getLocation(),
                userProfile.getCurrentCompany(),
                userProfile.getCurrentRole(),
                userProfile.getWebsite(),
                profileImageUrl(user),
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
                user.getEmail(),
                userProfile.getProfileId(),
                userProfile.getHeadline(),
                userProfile.getBio(),
                userProfile.getLocation(),
                userProfile.getCurrentCompany(),
                userProfile.getCurrentRole(),
                userProfile.getWebsite(),
                profileImageUrl(user),
                userId.equals(user.getUserId())
        );
    }

    public boolean createUserProfile(User user) {
        UserProfile profile = new UserProfile(user);
        UserProfile savedProfile = userProfileRepository.save(profile);
        return true;
    }

    public UserProfileResponse getProfileByUsername(String username, AuthenticatedUser authenticatedUser) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username " + username));
        UserProfile userProfile = userProfileRepository.findByUser_UserId(user.getUserId()).orElseThrow(() -> new IllegalArgumentException("profile not found"));

        boolean isOwner = authenticatedUser != null
                && Objects.equals(
                authenticatedUser.getUsername(),
                user.getUsername()
        );

        return new UserProfileResponse(
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                userProfile.getProfileId(),
                userProfile.getHeadline(),
                userProfile.getBio(),
                userProfile.getLocation(),
                userProfile.getCurrentCompany(),
                userProfile.getCurrentRole(),
                userProfile.getWebsite(),
                profileImageUrl(user),
                isOwner
        );
    }

    public ProfileImageData getProfileImage(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getProfileImageData() != null && user.getProfileImageContentType() != null) {
            return new ProfileImageData(user.getProfileImageData(), user.getProfileImageContentType());
        }
        if (neonObjectStorageService.isNeonObjectUrl(user.getProfileImageUrl())) {
            return neonObjectStorageService.downloadProfileImage(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Stored profile image not found"));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile image not found");
    }

    private ValidatedProfileImage validateProfileImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile image must not be empty");
        }
        if (file.getSize() > MAX_PROFILE_IMAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Profile image must be 5 MB or smaller");
        }

        byte[] imageData;
        try {
            imageData = file.getBytes();
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read profile image", exception);
        }

        String contentType = detectProfileImageType(imageData);
        if (contentType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPEG, PNG, and WebP images are supported");
        }

        String declaredContentType = file.getContentType();
        if (declaredContentType != null
                && !contentType.equalsIgnoreCase(declaredContentType.split(";", 2)[0].trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile image content type does not match its data");
        }
        return new ValidatedProfileImage(imageData, contentType);
    }

    private String profileImageUrl(User user) {
        if (user.getProfileImageData() != null) {
            return "/api/user-profile/profile-image/" + user.getUserId();
        }
        if (neonObjectStorageService.isNeonObjectUrl(user.getProfileImageUrl())) {
            return neonObjectStorageService.generateProfileImageUrl(user.getUserId())
                    .orElse(user.getProfileImageUrl());
        }
        return user.getProfileImageUrl();
    }

    private String detectProfileImageType(byte[] data) {
        if (startsWith(data, PNG_SIGNATURE)) {
            return "image/png";
        }
        if (data.length >= 3
                && data[0] == (byte) 0xFF
                && data[1] == (byte) 0xD8
                && data[2] == (byte) 0xFF) {
            return "image/jpeg";
        }
        if (data.length >= 12
                && Arrays.equals(Arrays.copyOfRange(data, 0, 4), new byte[]{'R', 'I', 'F', 'F'})
                && Arrays.equals(Arrays.copyOfRange(data, 8, 12), new byte[]{'W', 'E', 'B', 'P'})) {
            return "image/webp";
        }
        return null;
    }

    private boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private record ValidatedProfileImage(byte[] data, String contentType) {
    }
}