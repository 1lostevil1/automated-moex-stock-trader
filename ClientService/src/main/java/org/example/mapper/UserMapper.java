package org.example.mapper;

import org.example.DTO.UserDTO;
import org.example.postgres.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO entityToDTO(UserEntity entity){
        return new UserDTO(entity.getId(),entity.getUserName(),entity.getPassword(), entity.getTgName(), entity.getToken());
    }

    public UserEntity DTOToEntity(UserDTO dto){
        return UserEntity.builder()
                .id(dto.id())
                .userName(dto.username())
                .tgName(dto.tg_name())
                .token(dto.token())
                .build();
    }
}
