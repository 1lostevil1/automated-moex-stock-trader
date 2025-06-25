package org.example.DTO;

import java.util.Stack;
import java.util.UUID;

public record UserDTO(
        String id,
        String username,
        String password,
        String tg_name,
        String token
){}
