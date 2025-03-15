package com.integration.hubspot.service;

import com.integration.hubspot.dtos.ContactDTO;
import com.integration.hubspot.model.Contact;

public interface ICreateContactService {
    Contact createContact(ContactDTO contactDTO);
}
