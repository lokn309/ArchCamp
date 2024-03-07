package cn.lokn.knrpc.core.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

}
