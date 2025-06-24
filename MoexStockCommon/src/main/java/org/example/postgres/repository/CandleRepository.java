package org.example.postgres.repository;

import org.example.postgres.entity.CandleEntity;
import org.example.postgres.mapper.CandleRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class CandleRepository {

    private final JdbcClient jdbcClient;
    private final RowMapper<CandleEntity> rowMapper = new CandleRowMapper();


    @Autowired
    public CandleRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(CandleEntity candleEntity) {
        String sql = "INSERT INTO candle (" +
                "figi, instrument_uid, time, " +
                "open_price, close_price, " +
                "high_price, low_price, volume, " +
                "rsi, macd, ema) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (figi, time) DO UPDATE SET " +
                "instrument_uid = EXCLUDED.instrument_uid, " +
                "open_price = EXCLUDED.open_price, " +
                "close_price = EXCLUDED.close_price, " +
                "high_price = EXCLUDED.high_price, " +
                "low_price = EXCLUDED.low_price, " +
                "volume = EXCLUDED.volume";

        jdbcClient.sql(sql).params(
                candleEntity.getFigi(),
                candleEntity.getInstrumentUid(),
                Timestamp.valueOf(candleEntity.getTime().toLocalDateTime()),
                candleEntity.getOpenPrice(),
                candleEntity.getClosePrice(),
                candleEntity.getHighPrice(),
                candleEntity.getLowPrice(),
                candleEntity.getVolume(),
                candleEntity.getRsi(),
                candleEntity.getMacd(),
                candleEntity.getEma()
        ).update();
    }


    public List<CandleEntity> getByFigiWithLimit(String figi, int limit) {
        String sql = """
                SELECT * FROM  
                ( 
                               SELECT * FROM candle WHERE figi = ? ORDER BY time DESC LIMIT ?
                ) 
                ORDER BY time ASC
                """;
        return jdbcClient.sql(sql).params(figi, limit).query(rowMapper).list();
    }


    public void updateRsi(String figi, BigDecimal rsi) {
        String sql = """
                WITH latest_candle AS (
                    SELECT id FROM candle WHERE figi = ? ORDER BY time DESC LIMIT 1
                )
                UPDATE candle
                SET rsi = ?
                WHERE id IN (SELECT id FROM latest_candle)
                """;
        jdbcClient.sql(sql).params(figi, rsi).update();
    }

    public void updateMacd(String figi, BigDecimal macd) {
        String sql = """
                WITH latest_candle AS (
                    SELECT id FROM candle WHERE figi = ? ORDER BY time DESC LIMIT 1
                )
                UPDATE candle
                SET macd = ?
                WHERE id IN (SELECT id FROM latest_candle)
                """;
        jdbcClient.sql(sql).params(figi, macd).update();
    }

    public void updateEma(String figi, BigDecimal ema) {
        String sql = """
                WITH latest_candle AS (
                    SELECT id FROM candle WHERE figi = ? ORDER BY time DESC LIMIT 1
                )
                UPDATE candle
                SET ema = ?
                WHERE id IN (SELECT id FROM latest_candle)
                """;
        jdbcClient.sql(sql).params(figi, ema).update();
    }

    public CandleEntity getLastCandleByFigi(String figi) {
        String sql = "SELECT * FROM candle WHERE figi = ? ORDER BY time DESC LIMIT 1";
        return jdbcClient.sql(sql)
                .params(figi)
                .query(rowMapper)
                .optional()
                .orElse(null);
    }

    public List<CandleEntity> getByFigi(String figi) {
        String sql = "SELECT * FROM candle WHERE figi = ? ORDER BY time";
        return jdbcClient.sql(sql)
                .params(figi)
                .query(rowMapper)
                .list();
    }
}

