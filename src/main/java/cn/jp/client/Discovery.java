package cn.jp.client;

import cn.jp.annotation.Consumer;
import cn.jp.client.proxy.RpcProxyFactory;
import cn.jp.registryCenter.zk.ZkDiscovery;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;


public class Discovery implements BeanPostProcessor, InitializingBean {

    private ZkDiscovery zkDiscovery;

    public ZkDiscovery getZkDiscovery() {
        return zkDiscovery;
    }

    public Discovery(ZkDiscovery zkDiscovery) {
        this.zkDiscovery = zkDiscovery;
    }

    public void setZkDiscovery(ZkDiscovery zkDiscovery) {
        this.zkDiscovery = zkDiscovery;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        zkDiscovery.watchNode();
    }

    public void injectConsumer(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field f : fields) {
            if(!f.isAccessible()) {
                f.setAccessible(true);
            }
            if (f.getAnnotation(Consumer.class) != null) {
                try {
                    Object obj = RpcProxyFactory.getInstance().getObject(f.getType());
                    f.set(o, obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        injectConsumer(bean);
        return bean;
    }
}
