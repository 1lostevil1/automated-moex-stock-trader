package org.example.controller.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CreateUserDto;
import org.example.exception.RepeatedRegistrationException;
import org.example.exception.WrongCredentialsException;
import org.example.models.request.AuthTokenRequest;
import org.example.models.request.RegistrationRequest;
import org.example.models.response.AuthTokenResponse;
import org.example.models.response.RegistrationResponse;
import org.example.service.UserService;
import org.example.utils.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Validated @RequestBody RegistrationRequest request) {
        if (userService.findByUsername(request.username()).isPresent()) {
            return new ResponseEntity<>(new RepeatedRegistrationException(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
        }

        var createUserDto = new CreateUserDto(request.username(), request.password(), request.telegramUsername());
        var result = userService.createUser(createUserDto);

        if (!result) {
            return new ResponseEntity<>(new RepeatedRegistrationException(HttpStatus.BAD_REQUEST.value(), "Ошибка регистрации, повторите позже"), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new RegistrationResponse(createUserDto.username(), createUserDto.telegramUsername()));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthToken(@Validated @RequestBody AuthTokenRequest request) {
        log.info("{} tried to sign in (requested token)", request.username());

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (BadCredentialsException e) {

            log.info("Wrong credentials (incorrect login/password)");
            return new ResponseEntity<>(new WrongCredentialsException(HttpStatus.UNAUTHORIZED.value(), "Incorrect login or password"), HttpStatus.UNAUTHORIZED);
        }

        var userDetails = userService.loadUserByUsername(request.username());

        var token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new AuthTokenResponse(token));
    }
}
