package com.integration.hubspot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthToken {
    @Id
    private String id = "hubspot";
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
}
