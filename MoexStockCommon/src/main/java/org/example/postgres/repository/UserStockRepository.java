package org.example.postgres.repository;

import org.example.postgres.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserStockRepository {
    private final JdbcClient jdbcClient;

    @Autowired
    public UserStockRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void subscribe(String id, String ticker) {
        String sql = "INSERT INTO user_stock (id,ticker) " +
                "VALUES (?, ?)";
        jdbcClient.sql(sql).params(id, ticker).update();
    }

    public void unsubscribe(String id, String ticker) {
        String sql = "DELETE FROM user_stock WHERE id = ? AND tiсker = ? ";
        jdbcClient.sql(sql).params(id, ticker).update();
    }

    public boolean exists(String id,String ticker) {
        String sql = "SELECT id FROM user_stock WHERE id = ? AND ticker = ?";
        return jdbcClient.sql(sql).param(id,ticker).query().rowSet().next();
    }

    public List<String> getById(String id) {
        String sql = "SELECT ticker FROM user_stock WHERE id = ?";
        return jdbcClient.sql(sql).param(id).query((rs, rowNumber) -> rs.getString("ticker")).stream().toList();
    }
}
