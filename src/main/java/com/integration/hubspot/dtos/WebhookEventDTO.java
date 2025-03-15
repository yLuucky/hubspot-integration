package com.integration.hubspot.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEventDTO {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("subscriptionId")
    private String subscriptionId;

    @JsonProperty("portalId")
    private Long portalId;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("occurredAt")
    private Long occurredAt;

    @JsonProperty("objectId")
    private Long objectId;

    @JsonProperty("propertyName")
    private String propertyName;

    @JsonProperty("propertyValue")
    private String propertyValue;

    @JsonProperty("changeSource")
    private String changeSource;

    @JsonProperty("sourceId")
    private String sourceId;

    @JsonProperty("properties")
    private Map<String, Object> properties;
}