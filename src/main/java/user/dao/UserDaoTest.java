package user.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import user.dao.counting.CountingConnectionMaker;
import user.dao.counting.CountingDaoFactory;
import user.domain.Account;
import user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        UserDao dao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("yeonjae");
        user.setName("이연재");
        user.setPassword("1234");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " 조회 성공");

        /////////////////////////////////////////////////////////////

        AccountDao accountDao = context.getBean("accountDao", AccountDao.class);

        Account account = new Account();
        account.setId(user.getId());
        account.setCash(123546);

        accountDao.add(account);

        System.out.println(account.getId() + " 등록 성공");

        Account account2 = accountDao.get(account.getId());
        System.out.println(account2.getCash());

        System.out.println(account2.getId() + " 조회 성공");

        ////////////////////////////////////////////////////////////////
//        ApplicationContext countingContext = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
//        UserDao countingUserDao = countingContext.getBean("userDao", UserDao.class);
//        User user3 = new User();
//        user3.setId("yeonjae333");
//        user3.setName("이연재3333");
//        user3.setPassword("4321");
//
//        countingUserDao.add(user3);
//
//        System.out.println(user3.getId() + " 등록 성공");
//
//        CountingConnectionMaker ccm = countingContext.getBean("connectionMaker", CountingConnectionMaker.class);
//        System.out.println("Connection counter : " + ccm.getCounter());
    }
}
