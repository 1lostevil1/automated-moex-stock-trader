package org.example.service;

import org.example.postgres.entity.StockEntity;
import org.example.postgres.entity.UserEntity;
import org.example.postgres.repository.StockRepository;
import org.example.postgres.repository.UserRepository;
import org.example.postgres.repository.UserStockRepository;
import org.example.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final UserStockRepository userStockRepository;
    private final StockRepository stockRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;

    @Autowired
    public StockService(UserStockRepository userStockRepository, StockRepository stockRepository, JwtTokenUtils jwtTokenUtils, UserRepository userRepository) {
        this.userStockRepository = userStockRepository;
        this.stockRepository = stockRepository;
        this.jwtTokenUtils = jwtTokenUtils;
        this.userRepository = userRepository;
    }

    public List<StockEntity> getStocks(){
        return stockRepository.getAll();
    }

    public List<String> getStocksByJwt(String jwt){
        String username = jwtTokenUtils.getUsername(jwt);
        UserEntity user =  userRepository.getByUsername(username).get();
        return userStockRepository.getById(user.getId());
    }
}
