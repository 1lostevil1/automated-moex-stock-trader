package org.example.exception;

public record RepeatedRegistrationException(
        int status,
        String message
) {
}
