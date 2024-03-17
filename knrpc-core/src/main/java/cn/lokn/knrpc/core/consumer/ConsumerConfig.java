package cn.lokn.knrpc.core.consumer;

import cn.lokn.knrpc.core.api.LoadBalancer;
import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.api.Router;
import cn.lokn.knrpc.core.cluster.RandomLoadBalancer;
import cn.lokn.knrpc.core.cluster.RoundRibonLoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/10 19:49
 */
@Configuration
public class ConsumerConfig {

    @Value("${knrpc.providers}")
    String servers;
    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    // 这里有一个技巧，当spring 容器启动完后，在进行服务的获取
    @Bean
    // 此处需要添加 Order 因为 ApplicationRunner 加载是有顺序的
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            System.out.println(" ===> consumerBootstrap starting....");
            consumerBootstrap.start();
            System.out.println(" ===> consumerBootstrap started....");
        };
    }

    @Bean
    public LoadBalancer loadBalancer() {
//        return LoadBalancer.Default;
        return new RoundRibonLoadBalancer();
    }

    @Bean
    public Router router() {
        return Router.Default;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
        return new RegistryCenter.StaticRegistryCenter(List.of(servers.split(",")));
    }

}
