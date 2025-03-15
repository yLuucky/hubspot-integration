package com.integration.hubspot.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {

    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "The first name must not be blank")
    private String firstName;

    @NotBlank(message = "The last name must not be blank")
    private String lastName;
}
