package learningtest.jdk;

import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReflectionTest {
    @Test
    public void simpleProxy(){
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Hello.class},
                new UppercaseHandler(new HelloTarget())
        );

        assertThat(proxiedHello.sayHello("Yeonjae"), is("HELLO YEONJAE"));
        assertThat(proxiedHello.sayHi("Yeonjae"), is("HI YEONJAE"));
        assertThat(proxiedHello.sayThankYou("Yeonjae"), is("THANK YOU YEONJAE"));
    }
}
