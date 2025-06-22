package org.example.postgres.repository;

import org.example.postgres.entity.StockEntity;
import org.example.postgres.mapper.StockRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockRepository {

    private final JdbcClient jdbcClient;
    private final RowMapper<StockEntity> rowMapper = new StockRowMapper();

    @Autowired
    public StockRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(StockEntity stock) {
        String sql = "INSERT INTO stock (figi, instrument_uid, ticker, name) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (figi) DO UPDATE SET instrument_uid = EXCLUDED.instrument_uid, " +
                "ticker = EXCLUDED.ticker, name = EXCLUDED.name";

        jdbcClient.sql(sql).params(
                stock.getFigi(),
                stock.getInstrumentUid(),
                stock.getTicker(),
                stock.getName()).update();
    }

    public List<StockEntity> getAll() {
        String sql = "SELECT * FROM stock";
        return jdbcClient.sql(sql).query(rowMapper).list();
    }
}
