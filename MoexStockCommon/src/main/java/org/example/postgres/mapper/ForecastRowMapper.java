package org.example.postgres.mapper;

import org.example.postgres.entity.ForecastEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset; // Импортируем ZoneOffset

public class ForecastRowMapper implements RowMapper<ForecastEntity> {

    @Override
    public ForecastEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ForecastEntity entity = new ForecastEntity();

        entity.setId(rs.getLong("id"));
        entity.setTicker(rs.getString("ticker"));
        entity.setClosePrice(rs.getBigDecimal("close_price"));
        entity.setLastPrice(rs.getBigDecimal("last_price"));
        entity.setTiming(rs.getTimestamp("timing").toInstant().atOffset(OffsetDateTime.now().getOffset()));

        return entity;
    }
}
