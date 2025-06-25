package org.example.postgres.mapper;

import org.example.postgres.entity.StockDataEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class StockDataRowMapper implements RowMapper<StockDataEntity> {

    @Override
    public StockDataEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        StockDataEntity entity = new StockDataEntity();

        entity.setId(rs.getLong("id"));
        entity.setFigi(rs.getString("figi"));
        entity.setInstrumentUid(rs.getString("instrument_uid"));
        entity.setTicker(rs.getString("ticker"));
        Timestamp ts = rs.getTimestamp("time");
        if (ts != null) {
            entity.setTime(ts.toInstant().atOffset(OffsetDateTime.now().getOffset()));
        }

        entity.setOpenPrice(rs.getBigDecimal("open_price"));
        entity.setClosePrice(rs.getBigDecimal("close_price"));
        entity.setHighPrice(rs.getBigDecimal("high_price"));
        entity.setLowPrice(rs.getBigDecimal("low_price"));

        entity.setVolume(rs.getLong("volume"));
        entity.setBidVolume(rs.getLong("bid_volume"));
        entity.setAskVolume(rs.getLong("ask_volume"));
        entity.setBuyVolume(rs.getLong("buy_volume"));
        entity.setSellVolume(rs.getLong("sell_volume"));

        entity.setRsi(rs.getBigDecimal("rsi"));
        entity.setMacd(rs.getBigDecimal("macd"));
        entity.setEma(rs.getBigDecimal("ema"));

        return entity;
    }
}