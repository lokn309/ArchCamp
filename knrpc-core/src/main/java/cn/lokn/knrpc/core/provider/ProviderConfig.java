package cn.lokn.knrpc.core.provider;

import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.registry.ZkRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc() {
        return new ZkRegistryCenter();
    }

}
