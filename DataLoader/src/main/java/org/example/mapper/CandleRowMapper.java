package org.example.mapper;

import org.example.postgres.entity.CandleEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class CandleRowMapper implements RowMapper<CandleEntity> {

    @Override
    public CandleEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        CandleEntity candle = new CandleEntity();

        candle.setId(rs.getLong("id")); // если есть поле id
        candle.setFigi(rs.getString("figi"));
        candle.setInstrumentUid(rs.getString("instrument_uid"));

        candle.setTime(rs.getTimestamp("time").toInstant().atOffset(OffsetDateTime.now().getOffset()));

        candle.setOpenPrice(rs.getBigDecimal("open_price"));
        candle.setClosePrice(rs.getBigDecimal("close_price"));
        candle.setHighPrice(rs.getBigDecimal("high_price"));
        candle.setLowPrice(rs.getBigDecimal("low_price"));
        candle.setVolume(rs.getLong("volume"));

        candle.setRsi(rs.getBigDecimal("rsi"));
        candle.setMacd(rs.getBigDecimal("macd"));
        candle.setEma(rs.getBigDecimal("ema"));

        return candle;
    }
}
