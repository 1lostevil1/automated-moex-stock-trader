package org.example.postgres.mapper;

import org.example.postgres.entity.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<UserEntity> {
    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserEntity.builder()
                .id(rs.getString("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .telegramId(rs.getLong("telegram_id"))
                .telegramName(rs.getString("telegram_name"))
                .investApiToken(rs.getString("invest_api_token"))
                .build();
    }
}
