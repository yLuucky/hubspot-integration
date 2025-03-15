package com.integration.hubspot.repository;

import com.integration.hubspot.model.OAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<OAuthToken, String> {
}