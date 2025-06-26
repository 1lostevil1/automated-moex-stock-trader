package org.example.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public record SetInvestApiTokenRequest(
        @NotNull
        @JsonProperty("invest_api_token")
        String investApiToken
) {
}
