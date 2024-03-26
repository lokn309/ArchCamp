package cn.lokn.knrpc.demo.consumer;

import cn.lokn.knrpc.core.test.TestZkServer;
import cn.lokn.knrpc.demo.provider.KnrpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest //(classes = {KnrpcDemoConsumerApplication.class})
class KnrpcDemoConsumerApplicationTests {

    static ApplicationContext context;

    static TestZkServer testZkServer = new TestZkServer();

    @BeforeAll
    static void init() {
        testZkServer.start();
        context = SpringApplication.run(KnrpcDemoProviderApplication.class,
                "--server.port=8091",
                "--knrpc.zkServer=localhost:2182",
                "--loggin.level.cn.lokn.knrpc=debug");
    }

    @Test
    void contextLoads() {
        System.out.println("tests");
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(context, () -> 1);
        testZkServer.stop();
    }

}
