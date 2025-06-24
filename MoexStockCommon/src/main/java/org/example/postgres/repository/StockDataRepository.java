package org.example.postgres.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.postgres.entity.StockDataEntity;
import org.example.postgres.mapper.StockDataRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
@Slf4j
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
            figi, instrument_uid, ticker, time,
            open_price, close_price, high_price, low_price,
            volume, bid_volume, ask_volume, buy_volume, sell_volume,
            rsi, macd, ema
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (ticker, time) DO UPDATE SET
            figi = EXCLUDED.figi,
            instrument_uid = EXCLUDED.instrument_uid,
            open_price = EXCLUDED.open_price,
            close_price = EXCLUDED.close_price,
            high_price = EXCLUDED.high_price,
            low_price = EXCLUDED.low_price,
            volume = EXCLUDED.volume,
            bid_volume = EXCLUDED.bid_volume,
            ask_volume = EXCLUDED.ask_volume,
            buy_volume = EXCLUDED.buy_volume,
            sell_volume = EXCLUDED.sell_volume,
            rsi = EXCLUDED.rsi,
            macd = EXCLUDED.macd,
            ema = EXCLUDED.ema
        """;

        jdbcClient.sql(sql).params(
                entity.getFigi(),
                entity.getInstrumentUid(),
                entity.getTicker(),
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


    public List<StockDataEntity> findByTickerFromTime(String ticker, OffsetDateTime from) {
        String sql = """
                SELECT * FROM stock_data
                WHERE ticker = ? AND time >= ?
                ORDER BY time ASC
                """;


        Timestamp time = Timestamp.from(from.toInstant());
        log.info(String.valueOf(time));
        return jdbcClient.sql(sql)
                .params(
                        ticker,
                        Timestamp.from(from.toInstant())
                )
                .query(rowMapper)
                .list();
    }

}