package org.example.mapper;

import org.example.dto.CreateUserDto;
import org.example.dto.UserDto;
import org.example.postgres.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto entityToDto(UserEntity entity){
        return new UserDto(entity.getId(),entity.getUsername(),entity.getPassword(), entity.getTelegramUsername(), entity.getInvestApiToken());
    }

    public UserEntity dtoToEntity(UserDto dto){
        return UserEntity.builder()
                .id(dto.id())
                .username(dto.username())
                .telegramUsername(dto.telegramUsername())
                .investApiToken(dto.token())
                .build();
    }

    public UserEntity createDtoToEntity(CreateUserDto dto) {
        return UserEntity.builder()
                .username(dto.username())
                .telegramUsername(dto.telegramUsername())
                .build();
    }
}
