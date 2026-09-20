package com.app.devchallenges.Entity;

import com.app.devchallenges.DTO.ChallengeRequest;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "challenges")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "challenge_id")
    private UUID challengeId;

    private String title;

    private String description;

    @Column(name = "actual_name")
    private String actualResult;

    @Column(name = "expected_result")
    private String expectedResult;

    private List<String> environment;

    private String contribution; //Personal Project,Team Project, Client Project, Open Source

    @Column(name = "attempts_xml", columnDefinition = "xml")
    @JdbcTypeCode(SqlTypes.SQLXML)
    private String attemptsXML;

    private String learnings;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long views;

    @PrePersist
    void onCreate(){
        if(createdAt == null) Instant.now();
        if(updatedAt == null) Instant.now();
    }


    public Challenge(UUID challengeId, String title, String description, String actualResult, String expectedResult, List<String> environment, String contribution, String attemptsXML, String learnings, Instant createdAt, Instant updatedAt, User user, Long views) {
        this.challengeId = challengeId;
        this.title = title;
        this.description = description;
        this.actualResult = actualResult;
        this.expectedResult = expectedResult;
        this.environment = environment;
        this.contribution = contribution;
        this.attemptsXML = attemptsXML;
        this.learnings = learnings;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
        this.views = views;
    }

    public Challenge() {}

    public UUID getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(UUID challengeId) {
        this.challengeId = challengeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public List<String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(List<String> environment) {
        this.environment = environment;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public String getAttemptsXML() {
        return attemptsXML;
    }

    public void setAttemptsXML(String attemptsXML) {
        this.attemptsXML = attemptsXML;
    }

    public String getLearnings() {
        return learnings;
    }

    public void setLearnings(String learnings) {
        this.learnings = learnings;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }
}
