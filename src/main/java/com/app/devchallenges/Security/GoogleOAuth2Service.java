package com.app.devchallenges.Security;

import com.app.devchallenges.Entity.User;
import com.app.devchallenges.Repository.UserRepository;
import com.app.devchallenges.Service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service {

    private final UserRepository userRepository;
    private final UserProfileService userProfileService;

    public User processGoogleUser(OAuth2User oauthUser) {

        String googleId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        Optional<User> existingUser =
                userRepository.findByGoogleId(googleId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // Check whether this email already belongs to a normal account
        Optional<User> emailUser =
                userRepository.findByEmail(email);

        if (emailUser.isPresent()) {

            User user = emailUser.get();

            user.setGoogleId(googleId);

            if (user.getProfileImageUrl() == null) {
                user.setProfileImageUrl(picture);
            }

            userRepository.save(user);

            return user;
        }

        // Create new user
        User user = new User();

        user.setGoogleId(googleId);
        user.setEmail(email);
        user.setFullName(name);
        user.setProfileImageUrl(picture);
        user.setUsername(generateUsername(email));

        // Google users don't have a local password
        user.setPasswordHash(null);
        user.setActive(true);
        User savedUser = userRepository.save(user);
        userProfileService.createUserProfile(savedUser);

        return savedUser;
    }

    private String generateUsername(String email) {

        String username =
                email.substring(0, email.indexOf("@"))
                        .toLowerCase()
                        .replaceAll("[^a-z0-9_]", "_");

        return username;
    }
}
