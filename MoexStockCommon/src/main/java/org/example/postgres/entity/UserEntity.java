package org.example.postgres.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    private String id;
    private String userName;
    private String password;
    private Long tgId;
    private String tgName;
    private String token;



}
