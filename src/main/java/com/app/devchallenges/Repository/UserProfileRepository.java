package com.app.devchallenges.Repository;

import com.app.devchallenges.Entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUser_UserId(UUID userId);
    Optional<UserProfile> findByUser_Username(String username);
}

