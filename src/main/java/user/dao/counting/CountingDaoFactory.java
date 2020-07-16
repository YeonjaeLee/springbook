package user.dao.counting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.dao.ConnectionMaker;
import user.dao.DConnectionMaker;
import user.dao.UserDao;

@Configuration
public class CountingDaoFactory {
    @Bean
    public UserDao userDao(){
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker(){
        return new CountingConnectionMaker((realConnectionMaker()));
    }

    @Bean
    public ConnectionMaker realConnectionMaker(){
        return new DConnectionMaker();
    }
}
