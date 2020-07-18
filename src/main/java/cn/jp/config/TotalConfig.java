package cn.jp.config;

import cn.jp.client.ClientCondition;
import cn.jp.client.Discovery;
import cn.jp.properties.TotalConfigurationProperties;
import cn.jp.registryCenter.zk.ZkDiscovery;
import cn.jp.registryCenter.zk.ZkRegistry;
import cn.jp.server.Registry;
import cn.jp.server.Server;
import cn.jp.server.ServerCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@EnableConfigurationProperties(TotalConfigurationProperties.class)
public class TotalConfig {
    // 注入属性类
    @Autowired
    private TotalConfigurationProperties totalConfigurationProperties;

    @Bean
    @Conditional(ServerCondition.class)
    public Server server() {
        Server server = new Server(totalConfigurationProperties.serverPort, totalConfigurationProperties.getChildThreadNum(), totalConfigurationProperties.zkAddress);
        return server;
    }

    @Bean
    @Conditional(ServerCondition.class)
    public ZkRegistry zkRegistry() {
        ZkRegistry zkRegistry = new ZkRegistry(totalConfigurationProperties.zkAddress);
        return zkRegistry;
    }

    @Bean
    @Conditional(ServerCondition.class)
    @DependsOn("zkRegistry")
    public Registry registry() {
        Registry registry = new Registry();
        registry.setServerPort(totalConfigurationProperties.serverPort);
        registry.setZkRegistry(zkRegistry());

        return registry;
    }


    @Bean
    @ConditionalOnMissingBean
    @Conditional(ClientCondition.class)
    public ZkDiscovery zkDiscovery() {
        System.out.println(totalConfigurationProperties.zkAddress);
        ZkDiscovery zkDiscovery = new ZkDiscovery(totalConfigurationProperties.zkAddress);
        return zkDiscovery;
    }

    @DependsOn("zkDiscovery")
    @Bean
    @Conditional(ClientCondition.class)
    public Discovery discovery() {
        Discovery discovery = new Discovery(zkDiscovery());
        return discovery;
    }
}
