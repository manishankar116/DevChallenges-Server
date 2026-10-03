package com.app.devchallenges.Service;

import com.app.devchallenges.DTO.AuthRequest;
import com.app.devchallenges.DTO.AuthResponse;
import com.app.devchallenges.DTO.RegisterRequest;
import com.app.devchallenges.Entity.User;
import com.app.devchallenges.Repository.UserRepository;
import com.app.devchallenges.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserProfileService userProfileService;

    public AuthResponse register(RegisterRequest request) {
        validateUniqueUser(request);

        User savedUser = createUser(request);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = generateToken(savedUser, userDetails);

        return new AuthResponse(token, savedUser.getUsername());
    }

    public AuthResponse provisionUser(RegisterRequest request) {
        validateUniqueUser(request);

        User savedUser = createUser(request);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = generateToken(savedUser, userDetails);

        return new AuthResponse(token, savedUser.getUsername());
    }

    public List<String> getAllUsernames() {
        return userRepository.findAllUsernames();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = generateToken(user, userDetails);

        return new AuthResponse(token, user.getUsername());
    }

    private String generateToken(User user, UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getUserId().toString());
        extraClaims.put("email", user.getEmail());

        return jwtService.generateToken(extraClaims, userDetails);
    }

    private void validateUniqueUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    private User createUser(RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullname());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        userProfileService.createUserProfile(savedUser);

        return savedUser;
    }
}
