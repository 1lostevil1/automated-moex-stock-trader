package org.example.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mapper.OrderbookRowMapper;
import org.example.postgres.entity.OrderbookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class OrderbookRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RowMapper<OrderbookEntity> rowMapper = new OrderbookRowMapper();

    @Autowired
    public OrderbookRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(OrderbookEntity orderbookEntity) {
        String sql = "INSERT INTO orderbook (figi, instrument_uid, depth, bids, asks, time) " +
                "VALUES (?, ?, ?, ?::jsonb, ?::jsonb, ?)";

        try {
            String bidsJson = objectMapper.writeValueAsString(orderbookEntity.getBids());
            String asksJson = objectMapper.writeValueAsString(orderbookEntity.getAsks());

            jdbcClient.sql(sql).params(
                    orderbookEntity.getFigi(),
                    orderbookEntity.getInstrumentUid(),
                    orderbookEntity.getDepth(),
                    bidsJson,
                    asksJson,
                    Timestamp.from(orderbookEntity.getTime().toInstant())
            ).update();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing bids/asks to JSON", e);
        }
    }

    public List<OrderbookEntity> getAll() {
        String sql = "SELECT * FROM orderbook";
        return jdbcClient.sql(sql).query(rowMapper).list();
    }
}