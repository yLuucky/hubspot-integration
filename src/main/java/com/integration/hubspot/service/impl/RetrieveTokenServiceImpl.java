package com.integration.hubspot.service.impl;

import com.integration.hubspot.config.HubspotConfig;
import com.integration.hubspot.dtos.TokenResponseDTO;
import com.integration.hubspot.service.IRetrieveTokenService;
import com.integration.hubspot.util.HubspotApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RetrieveTokenServiceImpl implements IRetrieveTokenService {

    private final HubspotConfig hubspotConfig;

    @Autowired
    public RetrieveTokenServiceImpl(final HubspotConfig hubspotConfig) {
        this.hubspotConfig = hubspotConfig;
    }

    @Override
    public TokenResponseDTO getToken(final String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", hubspotConfig.getClientId());
        formData.add("client_secret", hubspotConfig.getClientSecret());
        formData.add("redirect_uri", hubspotConfig.getRedirectUri());
        formData.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
        RestTemplate restTemplate = new RestTemplate();

        return HubspotApiUtil.executeApiCall(() -> {
            ResponseEntity<TokenResponseDTO> response = restTemplate.exchange(
                    hubspotConfig.getTokenUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    TokenResponseDTO.class
            );
            return HubspotApiUtil.handleResponse(response);
        });
    }

    @Override
    public TokenResponseDTO refreshToken(final String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", hubspotConfig.getClientId());
        body.add("client_secret", hubspotConfig.getClientSecret());
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        return HubspotApiUtil.executeApiCall(() -> {
            ResponseEntity<TokenResponseDTO> response = restTemplate.exchange(
                    hubspotConfig.getTokenUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    TokenResponseDTO.class
            );
            return HubspotApiUtil.handleResponse(response);
        });
    }


}
