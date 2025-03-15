package com.integration.hubspot.controller;

import com.integration.hubspot.dtos.WebhookEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.hubspot.exception.HubspotApiException;
import com.integration.hubspot.service.IValidateWebhookSignatureService;
import com.integration.hubspot.util.HubspotApiUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final IValidateWebhookSignatureService webhookService;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebhookController(final IValidateWebhookSignatureService webhookService,
                             final ObjectMapper objectMapper) {
        this.webhookService = webhookService;
        this.objectMapper = objectMapper;
    }

    /**
     * Recebimento de Webhook para Criação de Contatos:
     * Endpoint que escuta e processa eventos do tipo "contact.creation", enviados pelo webhook do HubSpot.
     *
     * @param request Requisição HTTP completa
     * @param signatureHeader Assinatura do webhook (header x-hubspot-signature-v3)
     * @param timestampHeader Timestamp da requisição (header x-hubspot-request-timestamp)
     * @return Resposta HTTP com status 200 se processado com sucesso
     */
    @PostMapping("/contacts")
    public ResponseEntity<String> handleContactCreationWebhook(final HttpServletRequest request,
                                                               @RequestHeader(value = "x-hubspot-signature-v3", required = true) final String signatureHeader,
                                                               @RequestHeader(value = "x-hubspot-request-timestamp", required = true) final String timestampHeader) {

        log.info("Received HubSpot webhook for creating contacts");

        return HubspotApiUtil.executeApiCall(() -> {
            try {
                String requestBodyString = request.getReader().lines().collect(Collectors.joining());
                webhookService.validateWebhookRequest(timestampHeader, signatureHeader, requestBodyString);

                List<WebhookEventDTO> events;
                if (requestBodyString.trim().startsWith("[")) {
                    events = objectMapper.readValue(requestBodyString, objectMapper.getTypeFactory().constructCollectionType(List.class, WebhookEventDTO.class));
                } else {
                    WebhookEventDTO singleEvent = objectMapper.readValue(requestBodyString, WebhookEventDTO.class);
                    events = List.of(singleEvent);
                }

                int contactCreationEvents = 0;
                for (WebhookEventDTO event : events) {
                    if ("contact.creation".equals(event.getEventType())) {
                        webhookService.processWebhookEvent(event);
                        contactCreationEvents++;
                    } else {
                        log.debug("Ignoring event not related to contact creation: {}", event.getEventType());
                    }
                }

                log.info("Processed {} contact creation events", contactCreationEvents);
                return ResponseEntity.ok("Processed " + contactCreationEvents + " contact creation events");

            } catch (IOException e) {
                throw new HubspotApiException(
                        "Erro ao ler o corpo da requisição: " + e.getMessage(),
                        e,
                        HttpStatus.BAD_REQUEST,
                        "REQUEST_READING_ERROR"
                );
            }
        });
    }
}