package cn.lokn.knrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: config provider properties
 * @author: lokn
 * @date: 2024/04/06 23:34
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "knrpc.provider")
public class ProviderProperties {

    Map<String, String> metas = new HashMap<>();

}
