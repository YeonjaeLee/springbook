package user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.fail;
import static user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test_applicationContext.xml")
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class UserServiceTest {
    @Autowired
    UserServiceImpl userServiceImpl;
    @Autowired
    UserDao userDao;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;
    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("aaa", "하나", "1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "aaa@gmail.com"),
                new User("bbb", "둘", "22", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "bbb@gmail.com"),
                new User("ccc", "셋", "333", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "ccc@gmail.com"),
                new User("ddd", "넷", "4444", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "ddd@gmail.com"),
                new User("eee", "다섯", "55555", Level.GOLD, 100, Integer.MAX_VALUE, "eee@gmail.com")
        );
    }

    @Test
    public void bean() {
        assertThat(this.userServiceImpl, is(notNullValue()));
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithLevel.setLevel(null);

        userServiceImpl.add(userWithLevel);
        userServiceImpl.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }

    @Test
    public void upgradeLevels() throws Exception  {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);
//        userDao.deleteAll();
//        for(User user : users) userDao.add(user);

        // mockTEST
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(2));
        checkUserAndLevel(updated.get(0), "bbb", Level.SILVER);
        checkUserAndLevel(updated.get(1), "ddd", Level.GOLD);
//        checkLevelUpgraded(users.get(0), false);
//        checkLevelUpgraded(users.get(1), true);
//        checkLevelUpgraded(users.get(2), false);
//        checkLevelUpgraded(users.get(3), true);
//        checkLevelUpgraded(users.get(4), false);

        // mockTEST
        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId(), is(expectedId));
        assertThat(updated.getLevel(), is(expectedLevel));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if(upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        }
        else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    // levelupgrade 도중 예외처리 발생시 모든 사용자 upgrade 취소 TEST
    @Test
    public void upgradeAllOrNothing() {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender(mailSender);

        // 다이내믹 프록시 적용 트랜잭션 TEST
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(testUserService);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern("upgradeLevels");

        UserService txUserService = (UserService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {UserService.class}, txHandler);

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        }
        catch (TestUserServiceException e) {}

        checkLevelUpgraded(users.get(1), false);
    }

    //Mockito 적용한 TEST
    @Test
    public void MockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        // 목 오브젝트 생성, 메소드의 리턴 값 설정, DI
        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        //User 타입의 오브젝트를 파라미터로 받으며 update() 메소드가 두 번 호출 됐는지 확인
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel(), is(Level.SILVER));
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel(), is(Level.GOLD));

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        // 파라미터 정밀검사 위해 캡처 가능
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
    }

    static class TestUserService extends UserServiceImpl {
        private String id;

        private TestUserService(String id) {
            this.id = id;
        }

        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {}

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<String>();

        public List<String> getRequests() {
            return requests;
        }

        public void send(SimpleMailMessage mailMessages) throws MailException {
            requests.add(mailMessages.getTo()[0]);
        }

        public void send(SimpleMailMessage[] mailMessages) throws MailException {

        }
    }

    static class MockUserDao implements UserDao {
        private List<User> users;
        private List<User> updated = new ArrayList();

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }

        public List<User> getAll() {
            return this.users;
        }

        public void update(User user) {
            updated.add(user);
        }

        // TEST에 사용하지 않는 메소드들
        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        public User get(String id) {
            throw new UnsupportedOperationException();
        }

        public int getCount() {
            throw new UnsupportedOperationException();
        }
    }
}
