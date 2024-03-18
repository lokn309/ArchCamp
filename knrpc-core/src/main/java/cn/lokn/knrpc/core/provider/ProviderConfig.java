package cn.lokn.knrpc.core.provider;

import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.consumer.ConsumerBootstrap;
import cn.lokn.knrpc.core.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/07 23:42
 */
@Configuration
public class ProviderConfig {

    @Bean
    ProviderBoostrap providerBoostrap() {
        return new ProviderBoostrap();
    }

    @Bean
    // 此处需要添加 Order 因为 ApplicationRunner 加载是有顺序的
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ProviderBoostrap providerBoostrap) {
        return x -> {
            System.out.println(" ===> providerBoostrap starting....");
            providerBoostrap.start();
            System.out.println(" ===> providerBoostrap started....");
        };
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc() {
        return new ZkRegistryCenter();
    }

}
