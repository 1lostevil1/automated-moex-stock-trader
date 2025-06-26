package org.example.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public record RegistrationRequest(
        @NotNull
        @JsonProperty("username") String username,

        @NotNull
        @JsonProperty("telegram_username") String telegramUsername,

        @NotNull
        @JsonProperty("password") String password
) {
}
