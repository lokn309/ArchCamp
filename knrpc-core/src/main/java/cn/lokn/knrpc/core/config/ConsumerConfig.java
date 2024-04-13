package cn.lokn.knrpc.core.config;

import cn.lokn.knrpc.core.api.*;
import cn.lokn.knrpc.core.cluster.GrayRouter;
import cn.lokn.knrpc.core.cluster.RoundRibonLoadBalancer;
import cn.lokn.knrpc.core.consumer.ConsumerBootstrap;
import cn.lokn.knrpc.core.filter.ContextParamsFilter;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/10 19:49
 */
@Slf4j
@Configuration
@Import({AppProperties.class, ConsumerProperties.class})
public class ConsumerConfig {

    @Autowired
    AppProperties appProperties;

    @Autowired
    ConsumerProperties consumerProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "apollo.bootstrap", value = "enabled")
    public ApolloChangedListener consumer_apolloChangedListener() {
        return new ApolloChangedListener();
    }

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
        return new RoundRibonLoadBalancer();
    }

    @Bean
    public Router<InstanceMeta> grayRouter() {
        return new GrayRouter(consumerProperties.getGrayRatio());
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public RegistryCenter consumer_rc() {
        return new ZkRegistryCenter();
    }

    @Bean
    public Filter deaultFilter() {
        return new ContextParamsFilter();
    }

    @Bean
    @RefreshScope // 当配置文件发生改变时，会重新加载
    public RpcContext rpcContext(@Autowired Router router,
                                 @Autowired LoadBalancer loadBalancer,
                                 @Autowired List<Filter> filters) {
        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);
        context.setConsumerProperties(consumerProperties);
        context.getParameters().put("app.id", appProperties.getId());
        context.getParameters().put("app.namespace", appProperties.getNamespace());
        context.getParameters().put("app.env", appProperties.getEnv());

        return context;
    }


}
