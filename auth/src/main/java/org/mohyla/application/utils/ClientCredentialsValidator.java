package org.mohyla.application.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClientCredentialsValidator {

    private final Environment env;

    public ClientCredentialsValidator(Environment env) {
        this.env = env;
    }


    public boolean validate(String clientId, String clientSecret) {
        String envKey = switch (clientId) {
            case "itinerary-main" -> "ITINERARY_MAIN_SECRET";
            case "payment-service" -> "PAYMENT_SERVICE_SECRET";
            default -> null;
        };
        if (envKey == null) return false;

        String expectedSecret = env.getProperty(envKey);
        return expectedSecret != null && expectedSecret.equals(clientSecret);
    }
}
