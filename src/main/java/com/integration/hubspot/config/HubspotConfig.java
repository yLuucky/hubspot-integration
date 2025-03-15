package com.integration.hubspot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class HubspotConfig {

    @Value("${hubspot.client-id}")
    private String clientId;

    @Value("${hubspot.client-secret}")
    private String clientSecret;

    @Value("${hubspot.redirect-uri}")
    private String redirectUri;

    @Value("oauth%20crm.objects.contacts.write%20crm.objects.contacts.read")
    private String scope;

    @Value("${hubspot.api-base-url}")
    private String apiBaseUrl;

    @Value("${hubspot.auth-url}")
    private String authUrl;

    @Value("${hubspot.token-url}")
    private String tokenUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
