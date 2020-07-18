package user.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import user.domain.Account;
import user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

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
    }
}
