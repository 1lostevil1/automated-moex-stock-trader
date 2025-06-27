package org.example.controller.management;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.InvestApiTokenAlreadyInUseException;
import org.example.models.request.SetInvestApiTokenRequest;
import org.example.models.request.TickerRequest;
import org.example.service.StockService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {

    private final UserService userService;
    private final StockService stockService;

    @Autowired
    public UserController(UserService userService, StockService stockService) {
        this.userService = userService;
        this.stockService = stockService;
    }


    @PostMapping("/secured/investapitoken")
    public ResponseEntity<?> setInvestApiToken(@RequestHeader("Authorization") String jwtToken, @Validated @RequestBody SetInvestApiTokenRequest request) {
        if (!userService.setInvestApiToken(request.investApiToken(), jwtToken)) {
            return new ResponseEntity<>(new InvestApiTokenAlreadyInUseException(HttpStatus.BAD_REQUEST.value(), "Токен уже используется другим пользователем"), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Токен успешно обновлен");
    }

    @PostMapping("/secured/subscribe")
    public ResponseEntity<?> subscribeTicker(@RequestHeader("Authorization") String jwtToken, @Validated @RequestBody TickerRequest request) {
        if (!userService.subscribe(request.ticker(), jwtToken)) {
            return new ResponseEntity<>(new InvestApiTokenAlreadyInUseException(HttpStatus.BAD_REQUEST.value(), "Вы уже подписаны на данный тикер"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("Вы подписались на тикер");
    }

    @PostMapping("/secured/unsubscribe")
    public ResponseEntity<?> unsubscribeTicker(@RequestHeader("Authorization") String jwtToken, @Validated @RequestBody TickerRequest request) {
        if (!userService.unsubscribe(request.ticker(), jwtToken)) {
            return new ResponseEntity<>(new InvestApiTokenAlreadyInUseException(HttpStatus.BAD_REQUEST.value(), "Вы еще не подписаны на данный тикер"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("Вы отписались от тикера");
    }

    @GetMapping("/stocks")
    public ResponseEntity<?> getStocks() {
        return ResponseEntity.ok(stockService.getStocks());
    }

    @GetMapping("/secured/stocksByJwt")
    public ResponseEntity<?> getStocksByJwt(@RequestHeader("Authorization") String jwtToken) {
        return ResponseEntity.ok(stockService.getStocksByJwt(jwtToken));
    }

}
