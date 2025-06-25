package org.example.DTO;

import java.util.Stack;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String username,
        String password,
        String tg_name,
        String token
){}
