package com.integration.hubspot.util;

import com.integration.hubspot.exception.HubspotApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.function.Supplier;

public class HubspotApiUtil {

    public static <T> T executeApiCall(Supplier<T> apiCall) {
        try {
            return apiCall.get();
        } catch (HttpClientErrorException e) {
            throw new HubspotApiException(
                    "Error calling the HubSpot API: " + e.getResponseBodyAsString(),
                    e,
                    e.getStatusCode(),
                    "HUBSPOT_CLIENT_ERROR"
            );
        } catch (HttpServerErrorException e) {
            throw new HubspotApiException(
                    "HubSpot server error: " + e.getResponseBodyAsString(),
                    e,
                    e.getStatusCode(),
                    "HUBSPOT_SERVER_ERROR"
            );
        } catch (RestClientException e) {
            throw new HubspotApiException(
                    "Communication error with the HubSpot API: " + e.getMessage(),
                    e,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "HUBSPOT_COMMUNICATION_ERROR"
            );
        } catch (Exception e) {
            throw new HubspotApiException(
                    "Unexpected error when calling the HubSpot API: " + e.getMessage(),
                    e,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "UNEXPECTED_ERROR"
            );
        }
    }

    public static <T> T handleResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new HubspotApiException(
                    "Unsuccessful HubSpot API response: " + response.getStatusCode(),
                    response.getStatusCode(),
                    "HUBSPOT_API_ERROR"
            );
        }
    }
}
