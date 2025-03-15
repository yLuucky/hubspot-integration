package com.integration.hubspot.service;

import com.integration.hubspot.dtos.WebhookEventDTO;

/**
 * Interface para o serviço responsável por validar e processar eventos de webhook do HubSpot
 */
public interface IValidateWebhookSignatureService {
    void processWebhookEvent(WebhookEventDTO event);
    void validateWebhookSignature(String signature, String requestBody);
    void validateTimestamp(String timestamp);
    void validateWebhookRequest(String timestamp, String signature, String requestBody);
}