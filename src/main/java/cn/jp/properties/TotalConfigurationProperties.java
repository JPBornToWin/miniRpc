package cn.jp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//@Component
@ConfigurationProperties(prefix = "mini-rpc")
public class TotalConfigurationProperties {
    public  String zkAddress;

    public  Integer serverPort;

    private Integer childThreadNum;

    public  boolean serverService;

    public  boolean clientService;

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getChildThreadNum() {
        return childThreadNum;
    }

    public void setChildThreadNum(Integer childThreadNum) {
        this.childThreadNum = childThreadNum;
    }

    public boolean isServerService() {
        return serverService;
    }

    public void setServerService(boolean serverService) {
        this.serverService = serverService;
    }

    public boolean isClientService() {
        return clientService;
    }

    public void setClientService(boolean clientService) {
        this.clientService = clientService;
    }
}
