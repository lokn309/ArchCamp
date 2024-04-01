package cn.lokn.knrpc.core.registry.zk;

import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.api.RpcException;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.meta.ServiceMeta;
import cn.lokn.knrpc.core.registry.ChangedListener;
import cn.lokn.knrpc.core.registry.Event;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/18 00:21
 */
@Slf4j
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;

    @Value("${knrpc.zkService}")
    String servers;

    @Value("${knrpc.zkRoot}")
    String root;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(servers)
                .namespace(root)
                .retryPolicy(retryPolicy)
                .build();
        log.info(" ===> zk client starting to server [" + servers + "/" + root + "].");
        client.start();
    }

    @Override
    public void stop() {
        log.info(" ===> zk client stop.");
        client.close();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            if (client.checkExists().forPath(servicePath) == null) {
                // 创建服务的持久化节点。 "service".getBytes() 是便于调试用，没有什么实际意义
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            log.info(" ===> register to zk: " + instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, instance.toMetas().getBytes());
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 判断服务是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            log.info(" ===> unregister from zk: " + instancePath);
            // quietly() 方法的作用是节点不存在也不报错
            // 删除实例
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/" + service.toPath();
        try {
            // 获取所有子节点
            final List<String> nodes = client.getChildren().forPath(servicePath);
            log.info(" ===> fetchAll from zk: " + servicePath);
            nodes.forEach(System.out::println);
            return mapInstance(servicePath, nodes);
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    @NotNull
    private List<InstanceMeta> mapInstance(String servicePath, List<String> nodes) {
        return nodes.stream().map(x -> {
            final String[] strs = x.split("_");
            final InstanceMeta instanceMeta = InstanceMeta.http(strs[0], Integer.valueOf(strs[1]));

            String nodePath = servicePath + "/" + x;
            final byte[] bytes;
            try {
                bytes = client.getData().forPath(nodePath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            final Map<String, String> params = JSONObject.parseObject(bytes, Map.class);
            params.forEach((k, v) -> System.out.println(k + " -> " + v));
            instanceMeta.setParameters(params);
            return instanceMeta;
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        // zk 自己的包，作为zk的镜像，用于缓冲zk服务端的数据
        final TreeCache cache = TreeCache.newBuilder(client, "/" + service.toPath())
                .setCacheData(true) // 设置缓存
                .setMaxDepth(2) // 深度设置为2
                .build();
        cache.getListenable().addListener(
                (curator, event) -> {
                    // 有任何节点变动，这里会执行
                    log.info("zk subscribe event:" + event);
                    List<InstanceMeta> nodes = fetchAll(service);
                    listener.fire(new Event((nodes)));
                }
        );
        cache.start();
    }
}
