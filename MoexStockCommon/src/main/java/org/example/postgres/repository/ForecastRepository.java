package org.example.postgres.repository;

import org.example.postgres.entity.ForecastEntity;
import org.example.postgres.mapper.ForecastRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class ForecastRepository {

    private final JdbcClient jdbcClient;
    private final RowMapper<ForecastEntity> rowMapper = new ForecastRowMapper();

    @Autowired
    public ForecastRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(ForecastEntity entity) {
        String sql = """
            INSERT INTO forecast_response (ticker, close_price, last_price, timing)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (ticker, timing) DO UPDATE SET
                close_price = EXCLUDED.close_price,
                last_price = EXCLUDED.last_price
            """;

        jdbcClient.sql(sql).params(
                entity.getTicker(),
                entity.getClosePrice(),
                entity.getLastPrice(),
                Timestamp.from(entity.getTiming().toInstant())
        ).update();
    }

    public List<ForecastEntity> getByTickerFromTime(String ticker, OffsetDateTime fromTime) {
        String sql = "SELECT * FROM forecast_response WHERE ticker = ? AND timing >= ? ORDER BY timing";

        return jdbcClient.sql(sql).params(
                ticker,
                Timestamp.from(fromTime.toInstant())
        ).query(rowMapper).list();
    }
}