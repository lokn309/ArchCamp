package cn.lokn.knrpc.core.registry;

import cn.lokn.knrpc.core.api.RegistryCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/18 00:21
 */
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .namespace("knrpc")
                .retryPolicy(retryPolicy)
                .build();
        System.out.println(" ===> zk client starting.");
        client.start();
    }

    @Override
    public void stop() {
        System.out.println(" ===> zk client stop.");
        client.close();
    }

    @Override
    public void register(String service, String instance) {
        String servicePath = "/" + service;
        try {
            if (client.checkExists().forPath(servicePath) == null) {
                // 创建服务的持久化节点。 "service".getBytes() 是便于调试用，没有什么实际意义
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance;
            System.out.println(" ===> register to zk: " + instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, String instance) {
        String servicePath = "/" + service;
        try {
            // 判断服务是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance;
            System.out.println(" ===> unregister to zk: " + instancePath);
            // quietly 不存在也不报错
            // 删除实例
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> fetchAll(String service) {
        return null;
    }
}
