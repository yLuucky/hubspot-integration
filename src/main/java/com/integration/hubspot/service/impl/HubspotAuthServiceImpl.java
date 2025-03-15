package com.integration.hubspot.service.impl;

import com.integration.hubspot.config.HubspotConfig;
import com.integration.hubspot.dtos.TokenResponseDTO;
import com.integration.hubspot.exception.HubspotApiException;
import com.integration.hubspot.model.OAuthToken;
import com.integration.hubspot.repository.TokenRepository;
import com.integration.hubspot.service.IHubspotAuthService;
import com.integration.hubspot.service.IRetrieveTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class HubspotAuthServiceImpl implements IHubspotAuthService {

    private final HubspotConfig hubspotConfig;
    private final TokenRepository tokenRepository;
    private final IRetrieveTokenService retrieveTokenService;

    private String state = UUID.randomUUID().toString();

    @Autowired
    public HubspotAuthServiceImpl(final HubspotConfig hubspotConfig,
                                  final TokenRepository tokenRepository,
                                  final IRetrieveTokenService retrieveTokenService) {
        this.hubspotConfig = hubspotConfig;
        this.tokenRepository = tokenRepository;
        this.retrieveTokenService = retrieveTokenService;
    }

    @Override
    public String generateAuthorizationUrl() {
        state = UUID.randomUUID().toString();

        return String.format("https://app.hubspot.com/oauth/authorize?client_id=%s&scope=%s&redirect_uri=%s&state=%s",
                hubspotConfig.getClientId(), hubspotConfig.getScope(), hubspotConfig.getRedirectUri(), state);
    }

    @Override
    public TokenResponseDTO processCallback(final String code, final String receivedState) {
        if (!state.equals(receivedState)) {
            throw new HubspotApiException("Invalid state", HttpStatus.BAD_REQUEST, "INVALID_STATE");
        }

        TokenResponseDTO tokenResponse = retrieveTokenService.getToken(code);

        tokenRepository.save(OAuthToken.builder()
                .id("hubspot")
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .expiresAt(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()))
                .build());

        return tokenResponse;
    }

    @Override
    public String getAccessToken() {
        Optional<OAuthToken> tokenOptional = tokenRepository.findById("hubspot");

        if (tokenOptional.isEmpty()) {
            throw new HubspotApiException(
                    "No token found. Please authorize the application first.",
                    HttpStatus.UNAUTHORIZED,
                    "NO_TOKEN"
            );
        }

        OAuthToken token = tokenOptional.get();
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return refreshToken(token.getRefreshToken());
        }

        return token.getAccessToken();
    }

    public String refreshToken(final String refreshToken) {
        TokenResponseDTO tokenResponse = retrieveTokenService.refreshToken(refreshToken);

        tokenRepository.save(OAuthToken.builder()
                .id("hubspot")
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .expiresAt(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()))
                .build());

        return tokenResponse.getAccessToken();
    }
}
