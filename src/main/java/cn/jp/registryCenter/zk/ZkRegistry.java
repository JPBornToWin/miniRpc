package cn.jp.registryCenter.zk;

import cn.jp.constant.ZkConstant;
import lombok.Getter;
import lombok.Setter;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class ZkRegistry {
    @Getter
    @Setter
    private String address;

    private CountDownLatch latch = new CountDownLatch(1);

    public ZkRegistry(String address) {
        this.address = address;
    }

    private void addRootNode(ZooKeeper zooKeeper) {
        try {
            if (Objects.isNull(zooKeeper.exists(ZkConstant.ROOT, false))) {
                zooKeeper.create(ZkConstant.ROOT, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createPersistentNode(ZooKeeper zooKeeper, String path, String data) {
        addRootNode(zooKeeper);
        try {
            if (Objects.isNull(zooKeeper.exists(path, false))) {
                zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createEphemeralNode(ZooKeeper zooKeeper, String path, String data) {
        addRootNode(zooKeeper);
        try {
            if (Objects.isNull(zooKeeper.exists(path, false))) {
                zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public void registryService(String interfaceName, String address) {
        ZooKeeper zooKeeper = connect();
        addRootNode(zooKeeper);
        String parentPath = new StringBuilder().append(ZkConstant.ROOT).
                append("/").
                append(interfaceName).toString();
        System.out.println(parentPath);
        System.out.println(parentPath + "/" + address);
        createPersistentNode(zooKeeper, parentPath, String.valueOf(System.currentTimeMillis()));
        createEphemeralNode(zooKeeper, parentPath + "/" + address,  String.valueOf(System.currentTimeMillis()));

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
