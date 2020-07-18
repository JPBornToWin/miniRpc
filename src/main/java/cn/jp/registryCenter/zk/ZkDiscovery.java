package cn.jp.registryCenter.zk;

import cn.jp.client.DataManage;
import cn.jp.constant.ZkConstant;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class ZkDiscovery {
    private String address;

    public ZkDiscovery(String address) {
        this.address = address;
    }

    private CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        ZkDiscovery zkDiscovery = new ZkDiscovery("127.0.0.1:2181");
        zkDiscovery.watchNode();
    }

    public Map<String, List<String>> watchNode() {
        ZooKeeper zk = connect();
        Map<String, List<String>> map = new HashMap<>();
        try {
            List<String> nodeList = zk.getChildren(ZkConstant.ROOT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        watchNode();
                    }
                }
            });

            if (Objects.nonNull(nodeList)) {
                nodeList.stream().forEach(node -> {
                    try {
                        List<String> serviceList = zk.getChildren(ZkConstant.ROOT + "/" + node, false);
                        map.put(node, serviceList);
                        DataManage.getINSTANCE().update(node, serviceList);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return map;
    }

    private ZooKeeper connect() {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(address, ZkConstant.TIME_OUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });

            latch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return zooKeeper;
    }

}
