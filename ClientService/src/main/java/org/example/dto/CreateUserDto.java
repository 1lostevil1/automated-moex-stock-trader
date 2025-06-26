package org.example.dto;

public record CreateUserDto(
        String username,
        String password,
        String telegramUsername
) {
}
