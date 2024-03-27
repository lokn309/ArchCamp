package cn.lokn.knrpc.core.provider;

import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/07 23:42
 */
@Slf4j
@Configuration
public class ProviderConfig {

    @Bean
    ProviderBoostrap providerBoostrap() {
        return new ProviderBoostrap();
    }

    @Bean
    ProviderInvoker providerInvoker(@Autowired ProviderBoostrap providerBoostrap) {
        return new ProviderInvoker(providerBoostrap);
    }

    /**
     * 使用 {@link ApplicationRunner} 是为了延迟注册，目的是为了在 spring 容器启动完成后，
     * 将 skeleton 中的生产者信息，注册到 zk 中。
     *
     * 此处需要添加 Order 因为 ApplicationRunner 加载是有顺序的
     *
     * @param providerBoostrap 生产者启动Bean
     * @return
     */
    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ProviderBoostrap providerBoostrap) {
        return x -> {
            log.info(" ===> providerBoostrap starting....");
            providerBoostrap.start();
            log.info(" ===> providerBoostrap started.");
        };
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc() {
        return new ZkRegistryCenter();
    }

}
