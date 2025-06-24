package org.example.postgres.repository;

import org.example.postgres.entity.State;
import org.example.postgres.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class UserRepository {
    private final JdbcClient jdbcClient;
    @Autowired
    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(UserEntity user){
        String sql = "INSERT INTO tg_user (tgid,username,state) " +
                "VALUES (?, ?, ?)";
        jdbcClient.sql(sql).params(user.getId(),user.getUserName(),"NONE").update();
    };

    public void setState(Long id, State state){
        String sql = "UPDATE tg_user SET state = ? WHERE tgid = ?";
        jdbcClient.sql(sql).params(state.name(),id).update();
    };

    public Optional<String> getState(Long id){
        String sql = "SELECT state FROM tg_user WHERE tgid = ?";
        var rs = jdbcClient.sql(sql).params(id).query().rowSet();
        if(rs.next())
            return Optional.of(rs.getString("state"));
        return Optional.empty();
    };

    public void register(Long id,String token){
        String sql = "UPDATE tg_user SET token = ? WHERE tgid = ?";
        jdbcClient.sql(sql).params(token,id).update();
    };

    public void subscribe(Long id,String ticker){
        String sql = "INSERT INTO user_stock (tgid,ticker) " +
                "VALUES (?, ?)";
        jdbcClient.sql(sql).params(id,ticker).update();
    };

    public void unsubscribe(Long id,String ticker){
        String sql = "DELETE FROM user_stock WHERE tgid = ? AND tiсker = ? ";
        jdbcClient.sql(sql).params(id,ticker).update();
    };

    public List<Long> getUsers(String ticker){
        String sql = "SELECT tgid FROM user_stock WHERE tiсker = ? ";
        return jdbcClient.sql(sql).params(ticker).query((rs,rowNumber)-> rs.getLong("tgid")).stream().toList();
    };
}
