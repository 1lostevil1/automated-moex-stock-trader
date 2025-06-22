package org.example.postgres.mapper;

import org.example.postgres.entity.TradeEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class TradeRowMapper implements RowMapper<TradeEntity> {

    @Override
    public TradeEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        TradeEntity.TradeDirection direction = TradeEntity.TradeDirection.valueOf(rs.getString("direction"));
        return TradeEntity.builder()
                .id(rs.getLong("id"))
                .figi(rs.getString("figi"))
                .instrumentUid(rs.getString("instrument_uid"))
                .direction(direction)
                .price(rs.getBigDecimal("price"))
                .quantity(rs.getLong("quantity"))
                .time(rs.getTimestamp("time").toInstant().atOffset(OffsetDateTime.now().getOffset()))
                .build();
    }
}
