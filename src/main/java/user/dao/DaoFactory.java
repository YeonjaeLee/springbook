//package user.dao;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.SimpleDriverDataSource;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class DaoFactory {
//    @Bean
//    public UserDao userDao() {
//        UserDao userDao = new UserDao();
//        userDao.setDataSource(dataSource());
//        return userDao;
////        return new UserDao(connectionMaker());
//    }
//    @Bean
//    public AccountDao accountDao() {
//        AccountDao accountDao = new AccountDao();
//        accountDao.setDataSource(dataSource());
//        return accountDao;
////        return new AccountDao(connectionMaker());
//    }
//    @Bean
//    public ConnectionMaker connectionMaker(){
//        return new DConnectionMaker();
//        //return new NConnectionMaker();
//    }
//
//    @Bean
//    public DataSource dataSource(){
//        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//
//        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
//        dataSource.setUrl("jdbc:mysql://localhost/springbook");
//        dataSource.setUsername("root");
//        dataSource.setPassword("1234");
//
//        return dataSource;
//    }
//}
