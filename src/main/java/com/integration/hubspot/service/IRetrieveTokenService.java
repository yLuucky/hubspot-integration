package com.integration.hubspot.service;

import com.integration.hubspot.dtos.TokenResponseDTO;

public interface IRetrieveTokenService {
    TokenResponseDTO getToken(String code);
    TokenResponseDTO refreshToken(String refreshToken);
}
