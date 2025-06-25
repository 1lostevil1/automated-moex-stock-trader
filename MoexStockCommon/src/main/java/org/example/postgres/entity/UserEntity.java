package org.example.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    private String id;
    private String username;
    private String password;
    private Long telegramId;
    private String telegramName;
    private String investApiToken;
}
