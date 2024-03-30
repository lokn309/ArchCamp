package cn.lokn.knrpc.core.test;

import lombok.SneakyThrows;
import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.utils.CloseableUtils;

/**
 * @description: 嵌入 zookeeper 模拟服务
 * @author: lokn
 * @date: 2024/03/25 20:50
 */
public class TestZkServer {

    TestingCluster cluster;

    @SneakyThrows
    public void start() {
        InstanceSpec instanceSpec = new InstanceSpec(null,
                2182, -1, -1, true,
                -1, -1, -1);
        cluster = new TestingCluster(instanceSpec);
        System.out.println("TestingZookeeperServer starting ...");
        cluster.start();
        cluster.getServers().forEach(s -> System.out.println(s.getInstanceSpec()));
        System.out.println("TestingZookeeperServer started.");
    }

    @SneakyThrows
    public void stop() {
        System.out.println("TestingZookeeperServer stopping ...");
        cluster.stop();
        CloseableUtils.closeQuietly(cluster);
        System.out.println("TestingZookeeperServer stopped.");
    }

}
