package org.example.postgres.mapper;

import org.example.postgres.entity.TradeEntity;
import org.example.postgres.entity.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;

public class UserRowMapper implements RowMapper<UserEntity> {
    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .userName(rs.getString("username"))
                .password(rs.getString("password"))
                .tgId(rs.getLong("tg_id"))
                .tgName(rs.getString("tg_name"))
                .token(rs.getString("token"))
                .build();
    }
}
