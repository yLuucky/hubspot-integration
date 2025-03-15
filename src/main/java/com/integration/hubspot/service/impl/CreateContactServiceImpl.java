package com.integration.hubspot.service.impl;

import com.integration.hubspot.config.HubspotConfig;
import com.integration.hubspot.dtos.ContactDTO;
import com.integration.hubspot.model.Contact;
import com.integration.hubspot.service.ICreateContactService;
import com.integration.hubspot.service.IHubspotAuthService;
import com.integration.hubspot.util.HubspotApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CreateContactServiceImpl implements ICreateContactService {

    private final HubspotConfig hubspotConfig;
    private final RestTemplate restTemplate;
    private final IHubspotAuthService authService;

    @Autowired
    public CreateContactServiceImpl(final HubspotConfig hubspotConfig,
                                    final RestTemplate restTemplate,
                                    final IHubspotAuthService authService) {
        this.hubspotConfig = hubspotConfig;
        this.restTemplate = restTemplate;
        this.authService = authService;
    }

    @Override
    public Contact createContact(final ContactDTO contactDTO) {
        String accessToken = authService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> properties = new HashMap<>();
        properties.put("email", contactDTO.getEmail());
        properties.put("firstname", contactDTO.getFirstName());
        properties.put("lastname", contactDTO.getLastName());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("properties", properties);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        return HubspotApiUtil.executeApiCall(() -> {
            String url = hubspotConfig.getApiBaseUrl() + "/crm/v3/objects/contacts";
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            final Map responseBody = HubspotApiUtil.handleResponse(response);
            return mapToContact(responseBody);
        });
    }

    @SuppressWarnings("unchecked")
    private Contact mapToContact(final Map responseBody) {
        Map<String, Object> properties = (Map<String, Object>) responseBody.get("properties");

        return Contact.builder()
                .email((String) properties.get("email"))
                .firstName((String) properties.get("firstname"))
                .lastName((String) properties.get("lastname"))
                .build();
    }
}
