package cn.lokn.knrpc.core.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/10 19:49
 */
@Configuration
public class ConsumerConfig {

    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    // 这里有一个技巧，当spring 容器启动完后，在进行服务的获取
    @Bean
    // 此处需要添加 Order 因为 ApplicationRunner 加载是有顺序的
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            System.out.println(" ===> consumerBootstrap starting....");
            consumerBootstrap.start();
            System.out.println(" ===> consumerBootstrap started....");
        };
    }

}
