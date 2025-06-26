package org.example.postgres.repository;

import org.example.postgres.entity.UserEntity;
import org.example.postgres.mapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcClient jdbcClient;
    private static final RowMapper<UserEntity> rowMapper = new UserRowMapper();

    @Autowired
    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public UserEntity getById(String id) {
        String sql = "SELECT id,username,password,telegram_id,telegram_username,invest_api_token FROM traders_user WHERE id = ?";
        return jdbcClient.sql(sql).params(id).query(rowMapper).single();
    }

    public Optional<UserEntity> getByUsername(String username) {
        String sql = "SELECT id,username,password,telegram_id,telegram_username,invest_api_token FROM traders_user WHERE username = ?";
        return jdbcClient.sql(sql).param(username).query(rowMapper).optional();
    }

    public UserEntity getByTelegramUsername(String telegramUsername) {
        String sql = "SELECT id,username,password,telegram_id,telegram_username,invest_api_token FROM traders_user WHERE telegram_username = ?";
        return jdbcClient.sql(sql).param(telegramUsername).query(rowMapper).single();
    }

    public Optional<UserEntity> getByInvestApiToken(String investApiToken) {
        String sql = "SELECT id,username,password,telegram_id,telegram_username,invest_api_token FROM traders_user WHERE invest_api_token = ?";
        return jdbcClient.sql(sql).param(investApiToken).query(rowMapper).optional();
    }

    public void create(UserEntity user) {
        String sql = "INSERT INTO telegram_user (id,username,password) " +
                "VALUES (?, ?, ?)";
        jdbcClient.sql(sql).params(user.getId(),
                user.getUsername(),
                user.getPassword()
        ).update();
    }

    public void update(UserEntity user) {
        String sql = "UPDATE traders_user SET username = ?, password = ?, telegram_id = ?, telegram_username = ?, invest_api_token = ? WHERE id = ?";
        jdbcClient.sql(sql).params(
                user.getUsername(),
                user.getPassword(),
                user.getTelegramId(),
                user.getTelegramUsername(),
                user.getInvestApiToken(),
                user.getId()
        ).update();
    }

    public List<Long> getUsers(String ticker) {
        String sql = "SELECT telegram_id FROM user_stock JOIN traders_user USING(id) WHERE ticker = ?";
        return jdbcClient.sql(sql).params(ticker).query((rs, rowNumber) -> rs.getLong("telegram_id")).stream().toList();
    }
}
