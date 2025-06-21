package org.example.postgres.repository;

import org.example.postgres.entity.ForecastRequed;
import org.example.postgres.entity.StockDataEntity;
import org.example.postgres.mapper.StockDataRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class StockDataRepository {

    private final JdbcClient jdbcClient;
    private final RowMapper<StockDataEntity> rowMapper = new StockDataRowMapper();

    @Autowired
    public StockDataRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(StockDataEntity entity) {
        String sql = """
                INSERT INTO stock_data (
                    figi, instrument_uid, time,
                    open_price, close_price, high_price, low_price,
                    volume, bid_volume, ask_volume, buy_volume, sell_volume,
                    rsi, macd, ema
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcClient.sql(sql).params(
                entity.getFigi(),
                entity.getInstrumentUid(),
                Timestamp.from(entity.getTime().toInstant()),
                entity.getOpenPrice(),
                entity.getClosePrice(),
                entity.getHighPrice(),
                entity.getLowPrice(),
                entity.getVolume(),
                entity.getBidVolume(),
                entity.getAskVolume(),
                entity.getBuyVolume(),
                entity.getSellVolume(),
                entity.getRsi(),
                entity.getMacd(),
                entity.getEma()
        ).update();
    }

    public List<String> getAllFigi(){
        String sql = "SELECT DISTINCT figi FROM stock;";
        return jdbcClient.sql(sql)
                .query((rs,rowNumber) -> rs.getString("figi"))
                .list();
    }

    public ForecastRequed getByFigiWithLimit(String figi, int limit) {
        String sql = """
                SELECT * FROM (
                    SELECT * FROM stock_data WHERE figi = ? ORDER BY time DESC LIMIT ?
                ) sub
                ORDER BY time ASC
                """;
        return new ForecastRequed(
                figi,
                jdbcClient.sql(sql).params(figi, limit).query(rowMapper).list());
    }

}