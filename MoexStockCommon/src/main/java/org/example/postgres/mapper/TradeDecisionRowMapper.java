package org.example.postgres.mapper;

import org.example.postgres.entity.TradeDecisionDirection;
import org.example.postgres.entity.TradeDecisionEntity;
import org.example.postgres.entity.TradeEntity;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class TradeDecisionRowMapper implements RowMapper<TradeDecisionEntity> {

    @Override
    public TradeDecisionEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return TradeDecisionEntity.builder()
                .id(rs.getLong("id"))
                .ticker(rs.getString("ticker"))
                .price(rs.getBigDecimal("price"))
                .lastPrice(rs.getBigDecimal("last_price"))
                .stopLoss(rs.getBigDecimal("stop_loss"))
                .takeProfit(rs.getBigDecimal("take_profit"))
                .direction(TradeDecisionDirection.valueOf(rs.getString("direction")))
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .build();
    }
}
