package org.example.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public record AuthTokenRequest(
        @NotNull
        @JsonProperty("username") String username,

        @NotNull
        @JsonProperty("password") String password
) {
}
