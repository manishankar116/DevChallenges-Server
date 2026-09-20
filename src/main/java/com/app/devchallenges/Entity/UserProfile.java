package com.app.devchallenges.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id")
    private UUID profileId;

    @Column(name = "headline", length = 255)
    private String headline;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "current_company", length = 150)
    private String currentCompany;

    @Column(name = "role", length = 150)
    private String currentRole;

    @Column(name = "website", length = 255)
    private String website;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public UserProfile(User user) {
        this.user = user;
    }
}
