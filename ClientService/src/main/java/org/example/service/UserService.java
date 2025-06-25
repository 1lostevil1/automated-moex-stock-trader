package org.example.service;

import lombok.AllArgsConstructor;
import org.example.DTO.UserDTO;
import org.example.mapper.UserMapper;
import org.example.postgres.entity.UserEntity;
import org.example.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    public final UserRepository repository;
    public final PasswordEncoder passwordEncoder;
    public final UserMapper userMapper;

    public UserEntity findByUsername(String username){
        return repository.getByName(username);
    }

    public void createUser(UserDTO userDTO){
        var entity = userMapper.DTOToEntity(userDTO);
        entity.setPassword(passwordEncoder.encode(userDTO.password()));
        repository.create(entity);
    }


}
