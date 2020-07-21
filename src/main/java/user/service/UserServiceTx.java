package user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import user.domain.User;

public class UserServiceTx implements UserService{
    // UserService를 구현한 다른 오브젝트를 DI받는다
    UserService userService;

    PlatformTransactionManager transactionManager;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void add(User user) {
        userService.add(user);
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
            userService.upgradeLevels();

            this.transactionManager.commit(status);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
