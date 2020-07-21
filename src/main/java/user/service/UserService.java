package user.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;

import java.util.List;

public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private PlatformTransactionManager transactionManager;
    private MailSender mailSender;
    UserDao userDao;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    public void upgradeLevels() {
        // JDBC 트랜잭션 추상 오브젝트 생성
//        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        // JTA 트랜잭션 추상 오브젝트 생성
//        PlatformTransactionManager txManager = new JtaTransactionManager();
        // 하이버네이트 트랜잭션 추상 오브젝트 생성
//        PlatformTransactionManager hbManager = new HibernateTransactionManager();
        // JPA 트랜잭션 추상 오브젝트 생성
//        PlatformTransactionManager jpaManager = new JpaTransactionManager();

        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if(canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            this.transactionManager.commit(status);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEMail(user);
    }

    private void sendUpgradeEMail(User user) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("mail.server.com");

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다");

        this.mailSender.send(mailMessage);

//        Properties props = new Properties();
//        props.put("mail.smtp.host", "mail.ksug.org");
//        Session s = Session.getInstance(props, null);
//
//        MimeMessage message = new MimeMessage(s);
//        try {
//            message.setFrom(new InternetAddress("useradmin@ksug.org"));
//            message.addRecipient(Message.RecipientType.TO,
//                    new InternetAddress(user.getEmail()));
//            message.setSubject("Upgrade 안내");
//            message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다");
//
//            Transport.send(message);
//        } catch (AddressException e) {
//            throw new RuntimeException(e);
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        } catch (javax.mail.MessagingException e) {
//            e.printStackTrace();
//        }
    }
}
