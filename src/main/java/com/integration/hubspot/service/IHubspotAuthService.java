package com.integration.hubspot.service;

import com.integration.hubspot.dtos.TokenResponseDTO;

public interface IHubspotAuthService {
    String generateAuthorizationUrl();
    TokenResponseDTO processCallback(String code, String receivedState);
    String getAccessToken();
    String refreshToken(String refreshToken);
}
