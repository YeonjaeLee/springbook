package user.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDao {
//    private DataSource dataSource;
//    private JdbcContext jdbcContext;
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);

//        this.dataSource = dataSource;
    }

//    public void setJdbcContext(JdbcContext jdbcContext) {
//        this.jdbcContext = jdbcContext;
//    }

    public void add(final User user) {
        this.jdbcTemplate.update("insert into users(id, name, password) values (?,?,?)", user.getId(), user.getName(), user.getPassword());
//        this.jdbcContext.workWithStatementStrategy(
//            new StatementStrategy() {
//                public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                    PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?,?,?)");
//                    ps.setString(1, user.getId());
//                    ps.setString(2, user.getName());
//                    ps.setString(3, user.getPassword());
//
//                    return ps;
//                }
//            }
//        );
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id},
                new RowMapper<User>() {
                    public User mapRow(ResultSet resultSet, int i) throws SQLException {
                        User user = new User();
                        user.setId(resultSet.getString("id"));
                        user.setName(resultSet.getString("name"));
                        user.setPassword(resultSet.getString("password"));
                        return user;
                    }
                });
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id",
                new RowMapper<User>() {
                    public User mapRow(ResultSet resultSet, int i) throws SQLException {
                        User user = new User();
                        user.setId(resultSet.getString("id"));
                        user.setName(resultSet.getString("name"));
                        user.setPassword(resultSet.getString("password"));
                        return user;
                    }
                });
    }

    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
//        return this.jdbcTemplate.query(new PreparedStatementCreator() {
//            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
//                return connection.prepareStatement("select count(*) from users");
//            }
//        }, new ResultSetExtractor<Integer>() {
//            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
//                resultSet.next();
//                return resultSet.getInt(1);
//            }
//        });
    }
}