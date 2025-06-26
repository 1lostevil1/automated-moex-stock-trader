package org.example.controller.management;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.InvestApiTokenAlreadyInUseException;
import org.example.models.request.SetInvestApiTokenRequest;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {

    private final UserService userService;

    @PatchMapping("/investapitoken")
    public ResponseEntity<?> setInvestApiToken(@RequestHeader("Authorization") String jwtToken, @Validated @RequestBody SetInvestApiTokenRequest request) {
        if (!userService.setInvestApiToken(request.investApiToken(), jwtToken)) {
            return new ResponseEntity<>(new InvestApiTokenAlreadyInUseException(HttpStatus.BAD_REQUEST.value(), "Токен уже используется другим пользователем"), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Токен успешно обновлен");
    }
}
