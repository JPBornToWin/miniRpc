package cn.jp.server;

import cn.jp.annotation.Producer;
import cn.jp.properties.TotalConfigurationProperties;
import cn.jp.registryCenter.zk.ZkRegistry;
import cn.jp.server.producer.ApplicationManage;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@Component
public class Registry implements ApplicationContextAware {

    // 注入属性类
    @Autowired
    private TotalConfigurationProperties totalProperties;

    @Autowired
    private ZkRegistry zkRegistry;

    private Integer serverPort;

    public ZkRegistry getZkRegistry() {
        return zkRegistry;
    }

    public void setZkRegistry(ZkRegistry zkRegistry) {
        this.zkRegistry = zkRegistry;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Producer.class);
        String address = null;
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String finalAddress = address + ":";
        beans.entrySet().stream().forEach(e -> {
            Object o = e.getValue();
            String interfaceName = o.getClass().getAnnotation(Producer.class).interfaceName();
            if (StringUtils.isEmpty(interfaceName)) {
                interfaceName = o.getClass().getInterfaces()[0].getName();
            }
            System.out.println("--------------zk register------------------");
            ApplicationManage.addBean(interfaceName, o);
            zkRegistry.registryService(interfaceName, finalAddress + totalProperties.serverPort);
        });
    }
}
