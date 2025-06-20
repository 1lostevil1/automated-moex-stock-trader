package org.example.postgres.repository;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.postgres.entity.OrderbookEntity;
import org.example.postgres.mapper.OrderbookRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
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

    public List<OrderbookEntity> findByFigiAndTimeRange(String figi, OffsetDateTime from, OffsetDateTime to) {
        String sql = """
        SELECT * FROM orderbook
        WHERE figi = ? AND time BETWEEN ? AND ?
        ORDER BY time ASC
        """;

        return jdbcClient.sql(sql)
                .params(
                        figi,
                        Timestamp.from(from.toInstant()),
                        Timestamp.from(to.toInstant())
                )
                .query(rowMapper)
                .list();
    }

}