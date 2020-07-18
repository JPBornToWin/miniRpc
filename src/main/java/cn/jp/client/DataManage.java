package cn.jp.client;

import cn.jp.common.RequestRpc;
import cn.jp.registryCenter.chooser.Chooser;
import cn.jp.registryCenter.chooser.Polling;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DataManage {
    private static DataManage INSTANCE;

    private DataManage(){}

    private Map<String, Chooser> chooserMap = new ConcurrentHashMap<>();


    private Map<String, FutureRpc> futureRpcMap = new ConcurrentHashMap<>();


    private Map<String, RequestRpc> requestRpcMap = new ConcurrentHashMap<>();

    private Map<String, ConcurrentHashMap<String, Client>> clientMap = new ConcurrentHashMap<>();

    private  ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public Map<String, FutureRpc> getFutureRpcMap() {
        return futureRpcMap;
    }

    public void setFutureRpcMap(Map<String, FutureRpc> futureRpcMap) {
        this.futureRpcMap = futureRpcMap;
    }

    public Map<String, RequestRpc> getRequestRpcMap() {
        return requestRpcMap;
    }

    public void setRequestRpcMap(Map<String, RequestRpc> requestRpcMap) {
        this.requestRpcMap = requestRpcMap;
    }

    public Map<String, CopyOnWriteArraySet<String>> getServices() {
        return services;
    }

    public void setServices(Map<String, CopyOnWriteArraySet<String>> services) {
        this.services = services;
    }

    private  Map<String, CopyOnWriteArraySet<String>> services = new ConcurrentHashMap<>();


    private void sendRequest(RequestRpc requestRpc) {
        //ip:port
        String producer = getProducer(requestRpc.getClassName());
        if (clientMap.containsKey(requestRpc.getClassName())) {
            Map<String, Client> clients = clientMap.get(producer);
            if (clients.size() > 0) {
                if (clients.containsKey(producer)) {
                    Client client = clients.get(producer);
                    if (client.getChannel().isActive()) {
                        client.getChannel().writeAndFlush(requestRpc);
                        return;
                    } else {
                        clients.remove(client);
                    }
                }
                Client client = new Client(producer);
                clients.put(producer, client);
                client.getChannel().writeAndFlush(requestRpc);
            } else {
                Client client = new Client(producer);
                clients.put(producer, client);
                client.getChannel().writeAndFlush(requestRpc);
            }
        } else {
            ConcurrentHashMap<String, Client> map = new ConcurrentHashMap<>();
            Client client = new Client(producer);
            map.put(producer, client);
            clientMap.put(requestRpc.getRequestId(), map);
            client.getChannel().writeAndFlush(requestRpc);
        }

        requestRpcMap.put(requestRpc.getRequestId(), requestRpc);
    }

    public static DataManage getINSTANCE() {
        if (INSTANCE == null) {
            synchronized (DataManage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataManage();
                }
            }
        }

        return INSTANCE;
    }


    public FutureRpc execute(RequestRpc requestRpc) {
        FutureRpc futureRpc = new FutureRpc();
        futureRpc.setRequestId(requestRpc.getRequestId());
        futureRpcMap.put(futureRpc.getRequestId(), futureRpc);
        threadPoolExecutor.execute(() -> sendRequest(requestRpc));
        return futureRpc;
    }

    public String getProducer(String serviceName) {
        List<String> list = services.get(serviceName).stream().collect(Collectors.toList());
        if (chooserMap.containsKey(serviceName)) {
            return list.get(chooserMap.get(serviceName).choose(list.size()));
        } else {
            Chooser chooser = new Polling();
            chooserMap.put(serviceName, chooser);
            return list.get(chooser.choose(list.size()));
        }
    }

    public  String getProducer(String serviceName, Chooser chooser) {
        List<String> list = services.get(serviceName).stream().collect(Collectors.toList());
        return list.get(chooser.choose(list.size()));
    }


    public void update(String serviceName, List<String> serviceValue) {
        CopyOnWriteArraySet<String> set = services.get(serviceName) == null ? new CopyOnWriteArraySet<>() : services.get(serviceName);
        serviceValue.parallelStream().forEach(set::add);
        services.put(serviceName, set);
    }

}
