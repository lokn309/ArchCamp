package cn.lokn.knrpc.demo.consumer;

import cn.lokn.knrpc.demo.provider.KnrpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootTest
class KnrpcDemoConsumerApplicationTests {

    static ApplicationContext context;

    @BeforeAll
    static void init() {
        context = SpringApplication.run(KnrpcDemoProviderApplication.class,
                "--server.port=8091");
    }

    @Test
    void contextLoads() {
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(context, () -> 1);
    }

}
