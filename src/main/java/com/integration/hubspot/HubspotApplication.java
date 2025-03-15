package com.integration.hubspot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

@Async
@SpringBootApplication
public class HubspotApplication {

	public static void main(String[] args) {
		SpringApplication.run(HubspotApplication.class, args);
	}

}
