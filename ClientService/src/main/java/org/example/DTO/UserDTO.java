package org.example.DTO;

public record UserDTO(
        String id,
        String username,
        String password,
        String tg_name,
        String token
){}
