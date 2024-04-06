package cn.lokn.knrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: lokn
 * @date: 2024/04/06 23:37
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "knrpc.app")
public class AppConfigProperties {

    private String id = "app1";
    private String namespace = "public";
    private String env = "dev";

}
