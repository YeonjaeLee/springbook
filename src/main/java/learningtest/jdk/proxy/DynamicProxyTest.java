package learningtest.jdk.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DynamicProxyTest {
    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Yeonjae"), is("HELLO YEONJAE"));
        assertThat(proxiedHello.sayHi("Yeonjae"), is("HI YEONJAE"));
        assertThat(proxiedHello.sayThankYou("Yeonjae"), is("THANK YOU YEONJAE"));
    }

    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Yeonjae"), is("HELLO YEONJAE"));
        assertThat(proxiedHello.sayHi("Yeonjae"), is("HI YEONJAE"));
        assertThat(proxiedHello.sayThankYou("Yeonjae"), is("Thank You Yeonjae"));
    }

    @Test
    public void classNamePointcutAdvisor() {
        // 포인트컷 준비
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            public ClassFilter getClassFilter() {
                return new ClassFilter() {
                    public boolean matches(Class<?> aClass) {
                        // 클래스 이름이 HelloT로 시작하는 것만 선정
                        return aClass.getSimpleName().startsWith("HelloT");
                    }
                };
            }
        };
        // 메소드 이름이 sayH로 시작하는 것만 선정
        classMethodPointcut.setMappedName("sayH*");

        // TEST
        // 적용클래스
        checkAdviced(new HelloTarget(), classMethodPointcut, true);

        // 적용클래스X
        class HelloWorld extends HelloTarget {};
        checkAdviced(new HelloWorld(), classMethodPointcut, false);

        // 적용클래스
        class HelloTYeonjae extends HelloTarget {};
        checkAdviced(new HelloTYeonjae(), classMethodPointcut, true);
    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello)pfBean.getObject();

        if(adviced) {
            assertThat(proxiedHello.sayHello("Yj"), is("HELLO YJ"));
            assertThat(proxiedHello.sayHi("Yj"), is("HI YJ"));
            assertThat(proxiedHello.sayThankYou("Yj"), is("Thank You Yj"));
        }
        else {
            assertThat(proxiedHello.sayHello("Yj"), is("Hello Yj"));
            assertThat(proxiedHello.sayHi("Yj"), is("Hi Yj"));
            assertThat(proxiedHello.sayThankYou("Yj"), is("Thank You Yj"));
        }
    }

    static class UppercaseAdvice implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            // 리플렉션 method와 달리 메소드 실행 시 타깃 오브젝트를 전달할 필요 없다. MethodInvocation는 메소드 정보와 함께 타깃 오브젝트 알고 있다.
            String ret = (String)invocation.proceed();
            return ret.toUpperCase();
        }
    }

    static interface Hello {
        String sayHello(String name);
        String sayHi(String name);
        String sayThankYou(String name);
    }

    static class HelloTarget implements Hello {
        public String sayHello(String name) {return "Hello " + name;}
        public String sayHi(String name) {return "Hi " + name;}
        public String sayThankYou(String name) {return "Thank You " + name;}
    }
}
