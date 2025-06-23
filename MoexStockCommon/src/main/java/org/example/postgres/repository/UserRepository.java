package org.example.postgres.repository;

import org.example.postgres.entity.State;
import org.example.postgres.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;


@Repository
public class UserRepository {
    private final JdbcClient jdbcClient;
    @Autowired
    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(UserEntity user){
        String sql = "INSERT INTO tg_user (tgid,username) " +
                "VALUES (?, ?)";
        jdbcClient.sql(sql).params(user.getId(),user.getUserName()).update();
    };

    public void setState(Long id, State state){
        String sql = "UPDATE user SET state = ? WHERE tgid = ?";
        jdbcClient.sql(sql).params(state.name(),id).update();
    };

    public String getState(Long id, State state){
        String sql = "SELECT state FROM user WHERE tgid = ?";
        var rs = jdbcClient.sql(sql).params(id).query();
        return rs.rowSet().getString("state");
    };

    public void register(Long id,String token){
        String sql = "UPDATE user SET token = ? WHERE tgid = ?";
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
}
