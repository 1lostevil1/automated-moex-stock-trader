package org.example.repository;

import org.example.mapper.StockRowMapper;
import org.example.postgres.entity.StockEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<StockEntity> rowMapper= new StockRowMapper();

    @Autowired
    public StockRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(StockEntity stock) {
        String sql = "INSERT INTO stock (figi, instrument_uid, ticker, name) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (figi) DO UPDATE SET instrument_uid = EXCLUDED.instrument_uid, " +
                "ticker = EXCLUDED.ticker, name = EXCLUDED.name";

        jdbcTemplate.update(sql,
                stock.getFigi(),
                stock.getInstrumentUid(),
                stock.getTicker(),
                stock.getName());
    }

    public List<StockEntity> getAll() {
        String sql = "SELECT * FROM stock";
        return jdbcTemplate.query(sql, rowMapper);
    }
}
