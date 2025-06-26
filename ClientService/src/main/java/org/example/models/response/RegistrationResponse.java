package org.example.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegistrationResponse(
        @JsonProperty("username") String username,
        @JsonProperty("telegram_username") String telegramUsername
) {
}
