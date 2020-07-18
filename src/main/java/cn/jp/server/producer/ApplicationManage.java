package cn.jp.server.producer;

import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationManage {
    private static Map<String, Object> beanMap = new ConcurrentHashMap<>();

    public static void addBean(String name, Object bean) {
        beanMap.put(name, bean);
    }

    public static Object getBean(String name) {
        return beanMap.get(name);

    }
}
