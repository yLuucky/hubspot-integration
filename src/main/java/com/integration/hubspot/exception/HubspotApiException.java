package com.integration.hubspot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class HubspotApiException extends RuntimeException {
    private final HttpStatusCode status;
    private final String errorCode;

    public HubspotApiException(String message, HttpStatusCode status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HubspotApiException(String message, Throwable cause, HttpStatusCode status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}
