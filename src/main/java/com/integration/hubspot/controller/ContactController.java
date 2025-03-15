package com.integration.hubspot.controller;

import com.integration.hubspot.dtos.ContactDTO;
import com.integration.hubspot.model.Contact;
import com.integration.hubspot.service.ICreateContactService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ICreateContactService createContactServiceImpl;

    @Autowired
    public ContactController(final ICreateContactService createContactServiceImpl) {
        this.createContactServiceImpl = createContactServiceImpl;
    }

    /**
     * Criação de Contatos:
     * Endpoint que faz a criação de um Contato no CRM através da API do HubSpot.
     *
     * @param contactDTO DTO com os dados do contato
     * @return Contato criado
     */
    @PostMapping
    public ResponseEntity<Contact> createContact(@Valid @RequestBody final ContactDTO contactDTO) {
        log.info("Creating contact: {}", contactDTO.getEmail());

        Contact createdContact = createContactServiceImpl.createContact(contactDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
    }
}
