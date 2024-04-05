package cn.lokn.knrpc.core.consumer;

import cn.lokn.knrpc.core.api.Filter;
import cn.lokn.knrpc.core.api.LoadBalancer;
import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.api.Router;
import cn.lokn.knrpc.core.cluster.GrayRouter;
import cn.lokn.knrpc.core.cluster.RoundRibonLoadBalancer;
import cn.lokn.knrpc.core.filter.ParamsFilter;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.File;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/10 19:49
 */
@Slf4j
@Configuration
public class ConsumerConfig {

    @Value("${knrpc.providers}")
    String servers;

    @Value("${app.grayRatio}")
    private int grayRatio;

    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    // 这里有一个技巧，当spring 容器启动完后，在进行服务的获取
    @Bean
    // 此处需要添加 Order 因为 ApplicationRunner 加载是有顺序的
    @Order(Integer.MIN_VALUE + 1)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            log.info(" ===> consumerBootstrap starting....");
            consumerBootstrap.start();
            log.info(" ===> consumerBootstrap started....");
        };
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
//        return LoadBalancer.Default;
        return new RoundRibonLoadBalancer();
    }

//    @Bean
//    public Router router() {
//        return Router.Default;
//    }

    @Bean
    public Router<InstanceMeta> grayRouter() {
        return new GrayRouter(grayRatio);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
        return new ZkRegistryCenter();
    }

//    @Bean
//    public Filter filter() {
//        return new CacheFilter();
//    }

    @Bean
    public Filter paramsFilter() {
        return new ParamsFilter();
    }

}
