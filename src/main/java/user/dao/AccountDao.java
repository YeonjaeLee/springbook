package user.dao;

import user.domain.Account;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDao {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void add(Account account) throws ClassNotFoundException, SQLException{
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "insert into accounts(id, cash) values(?,?)");
        ps.setString(1, account.getId());
        ps.setInt(2, account.getCash());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public Account get(String id) throws ClassNotFoundException, SQLException{
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "select * from accounts where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        Account account = new Account();
        account.setId(rs.getString("id"));
        account.setCash(rs.getInt("cash"));

        rs.close();
        ps.close();
        c.close();

        return account;
    }
}