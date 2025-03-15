package com.integration.hubspot.service.impl;

import com.integration.hubspot.config.HubspotConfig;
import com.integration.hubspot.dtos.WebhookEventDTO;
import com.integration.hubspot.exception.HubspotApiException;
import com.integration.hubspot.service.IValidateWebhookSignatureService;
import com.integration.hubspot.util.HubspotApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class ValidateWebhookSignatureServiceImpl implements IValidateWebhookSignatureService {

    private static final long MAX_ALLOWED_TIMESTAMP = 300000;
    private final HubspotConfig hubspotConfig;

    @Autowired
    public ValidateWebhookSignatureServiceImpl(final HubspotConfig hubspotConfig) {
        this.hubspotConfig = hubspotConfig;
    }

    @Async
    @Override
    public void processWebhookEvent(final WebhookEventDTO event) {
        log.info("Processing a webhook event: {}", event.getEventId());

        HubspotApiUtil.executeApiCall(() -> {
            if ("contact.creation".equals(event.getEventType())) {
                handleContactCreation(event);
            } else {
                log.warn("Event type not supported: {}", event.getEventType());
            }
            return null;
        });
    }

    private void handleContactCreation(final WebhookEventDTO event) {
        log.info("Contact created in HubSpot: ID={}", event.getObjectId());

        if (event.getProperties() != null) {
            log.debug("Contact properties: {}", event.getProperties());

            Object email = event.getProperties().get("email");
            Object firstName = event.getProperties().get("firstname");
            Object lastName = event.getProperties().get("lastname");

            log.info("Contact details - Email: {}, Nome: {} {}", email, firstName, lastName);
        }
    }

    @Override
    public void validateWebhookSignature(final String signature, final String requestBody) {
        HubspotApiUtil.executeApiCall(() -> {
            if (signature == null) {
                throw new HubspotApiException(
                        "Signature header is missing",
                        HttpStatus.UNAUTHORIZED,
                        "MISSING_SIGNATURE"
                );
            }

            try {
                Mac sha256Hmac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKey = new SecretKeySpec(
                        hubspotConfig.getClientSecret().getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                );
                sha256Hmac.init(secretKey);

                String calculatedSignature = Base64.getEncoder().encodeToString(
                        sha256Hmac.doFinal(requestBody.getBytes(StandardCharsets.UTF_8))
                );

                if (!isEqual(signature, calculatedSignature)) {
                    throw new HubspotApiException(
                            "Invalid webhook signature",
                            HttpStatus.UNAUTHORIZED,
                            "INVALID_SIGNATURE"
                    );
                }

                return true;
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new HubspotApiException(
                        "Error validating webhook signature: " + e.getMessage(),
                        e,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "SIGNATURE_VALIDATION_ERROR"
                );
            }
        });
    }

    @Override
    public void validateTimestamp(final String timestamp) {
        HubspotApiUtil.executeApiCall(() -> {
            if (timestamp == null) {
                throw new HubspotApiException(
                        "Timestamp header is missing",
                        HttpStatus.BAD_REQUEST,
                        "MISSING_TIMESTAMP"
                );
            }

            try {
                long timestampValue = Long.parseLong(timestamp);
                long currentTime = Instant.now().toEpochMilli();

                if (currentTime - timestampValue > MAX_ALLOWED_TIMESTAMP) {
                    throw new HubspotApiException(
                            String.format("Timestamp expired: %d (current: %d)", timestampValue, currentTime),
                            HttpStatus.BAD_REQUEST,
                            "EXPIRED_TIMESTAMP"
                    );
                }

                return true;
            } catch (NumberFormatException e) {
                throw new HubspotApiException(
                        "Invalid timestamp format: " + timestamp,
                        e,
                        HttpStatus.BAD_REQUEST,
                        "INVALID_TIMESTAMP_FORMAT"
                );
            }
        });
    }

    @Override
    public void validateWebhookRequest(final String timestamp, final String signature, final String requestBody) {
        HubspotApiUtil.executeApiCall(() -> {
            validateTimestamp(timestamp);
            validateWebhookSignature(signature, requestBody);

            return null;
        });
    }

    private boolean isEqual(final String a, final String b) {
        if (isNull(a) || isNull(b)) {
            return Objects.equals(a, b);
        }

        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);

        if (aBytes.length != bBytes.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }

        return result == 0;
    }
}