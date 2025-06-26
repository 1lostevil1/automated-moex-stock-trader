package org.example.exception;

public record InvestApiTokenAlreadyInUseException(
        int status,
        String message
) {
}
