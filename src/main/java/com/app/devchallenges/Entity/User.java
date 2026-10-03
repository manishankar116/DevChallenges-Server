package com.app.devchallenges.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, unique = true, length = 30)
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 chars")
    @Pattern(
            regexp = "^[a-z0-9_]+$",
            message = "Username can contain only letters, numbers and underscores"
    )
    private String username;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "profile_image_data", columnDefinition = "bytea")
    private byte[] profileImageData;

    @Column(name = "profile_image_content_type", length = 50)
    private String profileImageContentType;

    @Column(length = 160)
    private String passwordHash;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false, updatable = true)
    private Instant updatedAt;


    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if(updatedAt == null){
            updatedAt = Instant.now();
        }
    }

}
