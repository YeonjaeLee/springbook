package user.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import user.domain.User;
import java.sql.SQLException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class UserDaoTest {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private UserDao dao;
    User user1;
    User user2;
    User user3;

    @Before
    public void setUp(){
//        System.out.println(this.context);
//        System.out.println(this);
//        this.dao = this.context.getBean("userDao", UserDao.class);
        this.user1 = new User("aaa", "하나", "springno1");
        this.user2 = new User("bbb", "둘", "springno2");
        this.user3 = new User("ccc", "셋", "springno3");
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        System.out.println(user1.getId() + " users 등록 성공");

        User user2 = dao.get(user1.getId());

        assertThat(user2.getName(), is(user1.getName()));
        assertThat(user2.getPassword(), is(user1.getPassword()));

        /////////////////////////////////////////////////////////////
//        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
//        AccountDao accountDao = context.getBean("accountDao", AccountDao.class);
//
//        accountDao.deleteAll();
//        assertThat(accountDao.getCount(), is(0));
//
//        Account account = new Account(user.getId(), 123546);
//
//        accountDao.add(account);
//        assertThat(accountDao.getCount(), is(1));
//
//        System.out.println(account.getId() + " accounts 등록 성공");
//
//        Account account2 = accountDao.get(account.getId());
//
//        assertThat(account2.getCash(), is(account.getCash()));
    }

    @Test
    public void count() throws SQLException{
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        dao.add(user3);
        assertThat(dao.getCount(), is(3));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException{
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.get("unknown_id");
    }
}
