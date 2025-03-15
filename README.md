# Integration with HubSpot CRM

## Overview
This project implements a REST API for seamless integration with the HubSpot CRM platform. The API enables contact creation, OAuth authentication, and webhook processing for HubSpot events. Built with Java and Maven.

## Features
- Create and manage contacts in HubSpot CRM
- OAuth 2.0 authentication flow with HubSpot
- Webhook processing for real-time HubSpot events
- RESTful API design for easy integration

## Technologies
- Java 21
- Spring Boot
- Spring Security
- Maven

## Installation
```bash
# Clone the repository
git clone https://github.com/yourusername/hubspot-integration.git

# Navigate to the project directory
cd hubspot-integration

# Build with Maven
mvn clean install
```

## Configuration
Modify the `application.properties` file in the `src/main/resources` directory:

```properties
# HubSpot Configuration
hubspot.client-id=your_client_id
hubspot.client-secret=your_client_secret
hubspot.redirect-uri=your_redirect_uri

# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/hubspot_integration
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

## Usage
Run the application:
```bash
mvn spring-boot:run
```

## API Endpoints

### Authentication
- `GET /api/auth/url` - Initiate OAuth flow with HubSpot
- `GET /api/auth/callback` - Handle OAuth callback from HubSpot

### Contacts
- `POST /api/contacts` - Create a new contact

### Webhooks
- `POST /api/webhooks/contacts` - Process incoming HubSpot events (contatcs)

## Documentation
For detailed documentation, motivations for the technology and possible future enhancements, see [Documentation](https://lucky.gitbook.io/hubspotapi-integration).