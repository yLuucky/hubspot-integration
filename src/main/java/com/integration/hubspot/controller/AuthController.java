package com.integration.hubspot.controller;

import com.integration.hubspot.dtos.TokenResponseDTO;
import com.integration.hubspot.service.IHubspotAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IHubspotAuthService authService;

    @Autowired
    public AuthController(final IHubspotAuthService authService) {
        this.authService = authService;
    }

    /**
     * Geração da Authorization URL:
     * Endpoint responsável por gerar e retornar a URL de autorização para iniciar o
     * fluxo OAuth com o HubSpot.
     *
     * @return URL de autorização
     */
    @GetMapping("/url")
    public ResponseEntity<String> getAuthorizationUrl() {
        log.info("Generating authorization URLs");

        String authUrl = authService.generateAuthorizationUrl();
        return ResponseEntity.ok(authUrl);
    }

    /**
     * Processamento do Callback OAuth:
     * Endpoint que recebe o código de autorização fornecido pelo HubSpot e realiza a
     * troca pelo token de acesso.
     *
     * @param code Código de autorização
     * @param state Estado para verificação CSRF
     * @return Token de acesso
     */
    @GetMapping("/callback")
    public ResponseEntity<TokenResponseDTO> handleCallback(@RequestParam("code") final String code,
                                                           @RequestParam("state") final String state) {

        log.info("Processing OAuth callback with authorization code");

        TokenResponseDTO tokenResponse = authService.processCallback(code, state);
        return ResponseEntity.ok(tokenResponse);
    }
}
