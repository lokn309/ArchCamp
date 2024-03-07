package cn.lokn.knrpc.demo.provider;

import cn.lokn.knrpc.core.api.RpcRequest;
import cn.lokn.knrpc.core.api.RpcResponse;
import cn.lokn.knrpc.core.provider.ProviderBoostrap;
import cn.lokn.knrpc.core.provider.ProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
// 将 providerBootstrap 加载到spring容器中
@Import({ProviderConfig.class})
public class KnrpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnrpcDemoProviderApplication.class, args);
    }

    @Autowired
    ProviderBoostrap providerBoostrap;

    // 使用 http + json 来实现序列化和通信
    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerBoostrap.invoke(request);
    }

    /**
     * {@link ApplicationRunner} 是等spring 容器完全启动准备好后在执行
     *
     * @return
     */
    @Bean
    ApplicationRunner providerRun() {
        return x -> {
            final RpcRequest request = new RpcRequest();
            request.setService("cn.lokn.knrpc.demo.api.UserService");
            request.setMethod("findById");
            request.setArgs(new Object[]{100});

            final RpcResponse rpcResponse = providerBoostrap.invoke(request);
            System.out.println("return : " + rpcResponse.getData());
        };
    }

}
