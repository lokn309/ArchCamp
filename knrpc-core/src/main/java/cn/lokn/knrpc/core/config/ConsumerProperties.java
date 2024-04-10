package cn.lokn.knrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 消费者配置类
 * @author: lokn
 * @date: 2024/04/10 23:51
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "knrpc.consumer")
public class ConsumerProperties {

    private int reties = 1;
    private int timeout = 1000;
    private int faultLimit = 10;
    private int halfOpenInitialDelay = 10_000;
    private int halfOpenDelay = 60_000;
    private int grayRatio = 0;

}
