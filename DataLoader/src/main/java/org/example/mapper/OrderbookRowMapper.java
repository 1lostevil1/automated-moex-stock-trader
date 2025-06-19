package org.example.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.postgres.entity.OrderbookEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

public class OrderbookRowMapper implements RowMapper<OrderbookEntity> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OrderbookEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderbookEntity orderbookEntity = new OrderbookEntity();
        orderbookEntity.setId(rs.getLong("id"));
        orderbookEntity.setFigi(rs.getString("figi"));
        orderbookEntity.setInstrumentUid(rs.getString("instrument_uid"));
        orderbookEntity.setDepth(rs.getInt("depth"));
        orderbookEntity.setTime(rs.getTimestamp("time").toInstant().atOffset(OffsetDateTime.now().getOffset()));

        try {
            String bidsJson = rs.getString("bids");
            if (bidsJson != null) {
                List<OrderbookEntity.Order> bids = objectMapper.readValue(bidsJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, OrderbookEntity.Order.class));
                orderbookEntity.setBids(bids);
            }

            String asksJson = rs.getString("asks");
            if (asksJson != null) {
                List<OrderbookEntity.Order> asks = objectMapper.readValue(asksJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, OrderbookEntity.Order.class));
                orderbookEntity.setAsks(asks);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON for bids/asks", e);
        }
        return orderbookEntity;
    }
}
