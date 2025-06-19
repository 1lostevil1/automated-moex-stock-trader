package org.example.mapper;

import org.example.postgres.entity.StockEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockRowMapper implements RowMapper<StockEntity> {

    @Override
    public StockEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return StockEntity.builder()
                .figi(rs.getString("figi"))
                .instrumentUid(rs.getString("instrument_uid"))
                .ticker(rs.getString("ticker"))
                .name(rs.getString("name"))
                .build();
    }
}
