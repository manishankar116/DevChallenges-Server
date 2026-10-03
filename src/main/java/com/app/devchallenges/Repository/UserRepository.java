package com.app.devchallenges.Repository;

import com.app.devchallenges.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface  UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    @Query("select user.username from User user")
    List<String> findAllUsernames();

    boolean existsByEmail(String email);

    Optional<User> findByUserId(UUID userId);

    Optional<User> findByGoogleId(String googleId);
}
