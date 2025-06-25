package org.example.mapper;

import org.example.DTO.UserDTO;
import org.example.postgres.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO entityToDTO(UserEntity entity){
        return new UserDTO(entity.getId(),entity.getUsername(),entity.getPassword(), entity.getTelegramName(), entity.getInvestApiToken());
    }

    public UserEntity DTOToEntity(UserDTO dto){
        return UserEntity.builder()
                .id(dto.id())
                .username(dto.username())
                .telegramName(dto.tg_name())
                .investApiToken(dto.token())
                .build();
    }
}
