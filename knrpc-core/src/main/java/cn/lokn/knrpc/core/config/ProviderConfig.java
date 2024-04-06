package cn.lokn.knrpc.core.config;

import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.provider.ProviderBoostrap;
import cn.lokn.knrpc.core.provider.ProviderInvoker;
import cn.lokn.knrpc.core.registry.zk.ZkRegistryCenter;
import cn.lokn.knrpc.core.transport.SpringBootTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/07 23:42
 */
@Slf4j
@Configuration
@Import({AppConfigProperties.class, ProviderConfigProperties.class, SpringBootTransport.class})
public class ProviderConfig {

    @Value("${server.port}")
    private String port;

    @Autowired
    ProviderConfigProperties providerConfigProperties;

    @Autowired
    AppConfigProperties appConfigProperties;

    @Bean
    ProviderBoostrap providerBoostrap() {
        return new ProviderBoostrap(port, appConfigProperties, providerConfigProperties);
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
