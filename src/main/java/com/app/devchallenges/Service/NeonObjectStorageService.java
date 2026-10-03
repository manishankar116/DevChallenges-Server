package com.app.devchallenges.Service;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;


import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import com.app.devchallenges.DTO.ProfileImageData;

@Service
public class NeonObjectStorageService {

    private final String endpoint;
    private final String region;
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String bucket;
    private volatile S3Client s3Client;
    private volatile S3Presigner s3Presigner;

    public NeonObjectStorageService(
            @Value("${app.neon.storage.endpoint:}") String endpoint,
            @Value("${app.neon.storage.region:}") String region,
            @Value("${app.neon.storage.access-key-id:}") String accessKeyId,
            @Value("${app.neon.storage.secret-access-key:}") String secretAccessKey,
            @Value("${app.neon.storage.bucket:}") String bucket) {
        this.endpoint = endpoint;
        this.region = region;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.bucket = bucket;
    }

    public Optional<String> uploadProfileImage(UUID userId, byte[] imageData, String contentType) {
        if (!isConfigured()) {
            return Optional.empty();
        }

        String objectKey = "profile-images/" + userId + "/avatar";
        s3Client().putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(imageData));

        return Optional.of(endpoint.replaceAll("/+$", "") + "/" + bucket + "/" + objectKey);
    }

    public boolean isNeonObjectUrl(String imageUrl) {
        if (imageUrl == null || !isConfigured()) {
            return false;
        }
        String objectPrefix = endpoint.replaceAll("/+$", "") + "/" + bucket + "/profile-images/";
        return imageUrl.startsWith(objectPrefix);
    }

    public Optional<ProfileImageData> downloadProfileImage(UUID userId) {
        if (!isConfigured()) {
            return Optional.empty();
        }

        String objectKey = "profile-images/" + userId + "/avatar";
        ResponseBytes<GetObjectResponse> object = s3Client().getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .build());
        String contentType = object.response().contentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Optional.empty();
        }
        return Optional.of(new ProfileImageData(object.asByteArray(), contentType));
    }

    public Optional<String> generateProfileImageUrl(UUID userId) {
        if (!isConfigured()) {
            return Optional.empty();
        }

        String objectKey = "profile-images/" + userId + "/avatar";
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .build())
                .build();
        return Optional.of(s3Presigner().presignGetObject(presignRequest).url().toString());
    }

    private boolean isConfigured() {
        return !endpoint.isBlank()
                && !region.isBlank()
                && !accessKeyId.isBlank()
                && !secretAccessKey.isBlank()
                && !bucket.isBlank();
    }

    private S3Client s3Client() {
        S3Client currentClient = s3Client;
        if (currentClient == null) {
            synchronized (this) {
                currentClient = s3Client;
                if (currentClient == null) {
                    currentClient = S3Client.builder()
                            .endpointOverride(URI.create(endpoint))
                            .region(Region.of(region))
                            .credentialsProvider(StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                            .overrideConfiguration(ClientOverrideConfiguration.builder()
                                    .apiCallTimeout(Duration.ofSeconds(10))
                                    .apiCallAttemptTimeout(Duration.ofSeconds(5))
                                    .build())
                            .serviceConfiguration(S3Configuration.builder()
                                    .pathStyleAccessEnabled(true)
                                    .build())
                            .build();
                    s3Client = currentClient;
                }
            }
        }
        return currentClient;
    }

    private S3Presigner s3Presigner() {
        S3Presigner currentPresigner = s3Presigner;
        if (currentPresigner == null) {
            synchronized (this) {
                currentPresigner = s3Presigner;
                if (currentPresigner == null) {
                    currentPresigner = S3Presigner.builder()
                            .endpointOverride(URI.create(endpoint))
                            .region(Region.of(region))
                            .credentialsProvider(StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                            .serviceConfiguration(S3Configuration.builder()
                                    .pathStyleAccessEnabled(true)
                                    .build())
                            .build();
                    s3Presigner = currentPresigner;
                }
            }
        }
        return currentPresigner;
    }

    @PreDestroy
    public void close() {
        S3Client currentClient = s3Client;
        if (currentClient != null) {
            currentClient.close();
        }
        S3Presigner currentPresigner = s3Presigner;
        if (currentPresigner != null) {
            currentPresigner.close();
        }
    }
}
