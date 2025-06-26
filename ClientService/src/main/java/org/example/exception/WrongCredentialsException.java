package org.example.exception;

public record WrongCredentialsException(
        int status,
        String message
) {
}