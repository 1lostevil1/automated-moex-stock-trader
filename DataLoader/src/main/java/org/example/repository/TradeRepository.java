package org.example.repository;

import org.example.mapper.TradeRowMapper;
import org.example.postgres.entity.CandleEntity;
import org.example.postgres.entity.TradeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class TradeRepository {

    private final JdbcClient jdbcClient;

    private static final RowMapper<TradeEntity> rowMapper = new TradeRowMapper();

    @Autowired
    public TradeRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(TradeEntity trade) {
        String sql = "INSERT INTO trade (figi, instrument_uid, direction, price, quantity, time) " +
                "VALUES (?, ?, ?::trade_direction, ?, ?, ?)";
        jdbcClient.sql(sql).params(
                trade.getFigi(),
                trade.getInstrumentUid(),
                trade.getDirection().name(),
                trade.getPrice(),
                trade.getQuantity(),
                Timestamp.from(Instant.from(trade.getTime()))
        ).update();
    }

}
