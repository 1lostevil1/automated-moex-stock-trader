package org.example.dto;

public record UserDto(
        String id,
        String username,
        String password,
        String telegramUsername,
        String token
){}
