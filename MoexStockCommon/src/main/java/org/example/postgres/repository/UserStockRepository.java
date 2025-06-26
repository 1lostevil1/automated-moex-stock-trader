package org.example.postgres.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

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
}
