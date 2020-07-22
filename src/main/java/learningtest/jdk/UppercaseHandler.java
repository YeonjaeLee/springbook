package learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {
    Object target;

    public UppercaseHandler(Object target){
        this.target = target;
    }

    // 호출 메소드 리턴타입이 String && 메소드이름 say 붙은 경우에만 return
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(target, args);
        if(ret instanceof String && method.getName().startsWith("say")) {
            return ((String)ret).toUpperCase();
        }
        else {
            return ret;
        }
    }
}
