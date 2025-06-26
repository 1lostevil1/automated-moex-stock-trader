package org.example.postgres.repository;

import org.example.postgres.entity.TradeDecisionEntity;
import org.example.postgres.mapper.TradeDecisionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class TradeDecisionRepository {

    private final JdbcClient jdbcClient;
    private final RowMapper<TradeDecisionEntity> rowMapper = new TradeDecisionRowMapper();

    @Autowired
    public TradeDecisionRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(TradeDecisionEntity position) {
        String sql = """
            INSERT INTO trade_decision (
                ticker, price,last_price, stop_loss, take_profit, direction, created_at
            ) VALUES (?, ?, ?,?, ?, ?::trade_decision_direction, ?)
            ON CONFLICT (ticker) DO UPDATE SET
                price = EXCLUDED.price,
                last_price = EXCLUDED.last_price,
                stop_loss = EXCLUDED.stop_loss,
                take_profit = EXCLUDED.take_profit,
                direction = EXCLUDED.direction,
                created_at = EXCLUDED.created_at
            """;

        jdbcClient.sql(sql).params(
                position.getTicker(),
                position.getPrice(),
                position.getLastPrice(),
                position.getStopLoss(),
                position.getTakeProfit(),
                position.getDirection().name(),
                Timestamp.valueOf(position.getCreatedAt().toLocalDateTime())
        ).update();
    }

    public TradeDecisionEntity findByTicker(String ticker) {
        String sql = "SELECT * FROM trade_decision WHERE ticker = ?";
        return jdbcClient.sql(sql).params(ticker).query(rowMapper).optional().orElse(null);
    }
}