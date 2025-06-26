package org.example.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public record TickerRequest(
        @NotNull
        @JsonProperty("ticker")
        String ticker
){}
