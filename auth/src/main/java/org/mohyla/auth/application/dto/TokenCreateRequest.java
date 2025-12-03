package org.mohyla.auth.application.dto;

import java.io.Serializable;

public record TokenCreateRequest(String clientId, String clientSecret)  {}
