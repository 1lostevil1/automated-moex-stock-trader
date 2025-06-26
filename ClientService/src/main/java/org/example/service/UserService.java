package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CreateUserDto;
import org.example.dto.UserDto;
import org.example.mapper.UserMapper;
import org.example.postgres.entity.UserEntity;
import org.example.postgres.repository.UserRepository;

import org.example.postgres.repository.UserStockRepository;
import org.example.utils.JwtTokenUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserStockRepository userStockRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var entity = userRepository.getByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User '%s' not found",username)));

        return new User(entity.getUsername(), entity.getPassword(), List.of());
    }

    public Optional<UserDto> findByUsername(String username) {
        var entityOpt = userRepository.getByUsername(username);

        return entityOpt.map(userMapper::entityToDto);

    }

    public boolean createUser(CreateUserDto dto){
        try {
            var entity = userMapper.createDtoToEntity(dto);
            entity.setId(UUID.randomUUID().toString());
            entity.setPassword(passwordEncoder.encode(dto.password()));

            userRepository.create(entity);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean setInvestApiToken(String investApiToken, String jwtToken) {
        var entityOpt = userRepository.getByInvestApiToken(investApiToken);

        if (entityOpt.isPresent()) {
            return false;
        }

//      TODO надо как-то обрабатывать, если передали невалидный по username токен
        var entity = getUserEntityOutOfJwt(jwtToken).orElseThrow();
        entity.setInvestApiToken(investApiToken);

        userRepository.update(entity);
        return true;
    }

    private Optional<UserEntity> getUserEntityOutOfJwt(String token) {
        String username = jwtTokenUtils.getUsername(token);
        return userRepository.getByUsername(username);
    }

    public boolean subscribe(String token, String ticker){
        var entity = getUserEntityOutOfJwt(token).orElseThrow();
        if(userStockRepository.exists(entity.getId(),token))
            return false;
        userStockRepository.subscribe(entity.getId(),token);
        return true;
    }

    public boolean unsubscribe(String token, String ticker){
        var entity = getUserEntityOutOfJwt(token).orElseThrow();
        if(!userStockRepository.exists(entity.getId(),token))
            return false;
        userStockRepository.unsubscribe(entity.getId(),token);
        return true;
    }
}
