package org.example.postgres.repository;

import org.example.postgres.entity.State;
import org.example.postgres.entity.UserEntity;
import org.example.postgres.mapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public class UserRepository {
    private final JdbcClient jdbcClient;
    private static final RowMapper<UserEntity> rowMapper = new UserRowMapper();
    @Autowired
    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public UserEntity getById(UUID id){
        String sql = "SELECT id,username,password,tg_id,tg_name,token FROM tg_user WHERE id = ?";
        return jdbcClient.sql(sql).params(id).query(rowMapper).single();
    }

    public UserEntity getByName(String name){
        String sql = "SELECT id,username,password,tg_id,tg_name,token FROM tg_user WHERE username = ?";
        return jdbcClient.sql(sql).params(name).query(rowMapper).single();
    }

    public UserEntity getByTelegramName(String name){
        String sql = "SELECT id,username,password,tg_id,tg_name,token FROM tg_user WHERE tg_name = ?";
        return jdbcClient.sql(sql).params(name).query(rowMapper).single();
    }

    public void create(UserEntity user){
        String sql = "INSERT INTO tg_user (id,username,password) " +
                "VALUES (?, ?, ?)";
        jdbcClient.sql(sql).params(user.getId(),
                user.getUserName(),
                user.getPassword()
                ).update();
    };

    public void update(UserEntity user){
        String sql = "UPDATE tg_user SET username = ?, password = ?, tg_id = ?, tg_name = ?, token = ? WHERE id = ?)";
        jdbcClient.sql(sql).params(
                user.getUserName(),
                user.getPassword(),
                user.getTgId(),
                user.getTgName(),
                user.getToken(),
                user.getId()
        ).update();
    }

    public void subscribe(String id,String ticker){
        String sql = "INSERT INTO user_stock (id,ticker) " +
                "VALUES (?, ?)";
        jdbcClient.sql(sql).params(id,ticker).update();
    }

    public void unsubscribe(String id,String ticker){
        String sql = "DELETE FROM user_stock WHERE id = ? AND tiсker = ? ";
        jdbcClient.sql(sql).params(id,ticker).update();
    }

    public List<Long> getUsers(String ticker){
        String sql = "SELECT tgid FROM user_stock JOIN tg_user USING(id) WHERE ticker = ? ";
        return jdbcClient.sql(sql).params(ticker).query((rs,rowNumber)-> rs.getLong("tgid")).stream().toList();
    }
}
