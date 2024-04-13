package cn.lokn.knrpc.demo.provider;

import cn.lokn.knrpc.core.api.RpcRequest;
import cn.lokn.knrpc.core.api.RpcResponse;
import cn.lokn.knrpc.core.config.ProviderConfig;
import cn.lokn.knrpc.core.config.ProviderProperties;
import cn.lokn.knrpc.core.transport.SpringBootTransport;
import cn.lokn.knrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    UserService userService;

    @Autowired
    SpringBootTransport transport;

    @Autowired
    ProviderProperties providerProperties;

    @RequestMapping("/timeoutPorts")
    public RpcResponse<Object> setTimeoutPorts(@RequestParam("timeoutPorts") String timeoutPorts) {
        userService.setTimeoutPorts(timeoutPorts);
        RpcResponse<Object> response = new RpcResponse<>();
        response.setStatus(true);
        response.setData("OK");
        return response;
    }

    @RequestMapping("/meta")
    public String metas() {
        return providerProperties.getMetas().toString();
    }

    /**
     * {@link ApplicationRunner} 是等spring 容器完全启动准备好后在执行
     *
     * @return
     */
    @Bean
    ApplicationRunner providerRun() {
        return x -> {
            // test 1 parameter method
            final RpcRequest request = new RpcRequest();
            request.setService("cn.lokn.knrpc.demo.api.UserService");
            request.setMethodSign("findById@1_int");
            request.setArgs(new Object[]{100});

//            final RpcResponse userRpcResponse = providerInvoker.invoke(request);
//            System.out.println("user return : " + userRpcResponse.getData());
//
//            // test 2 parameters method
//            request.setService("cn.lokn.knrpc.demo.api.UserService");
//            request.setMethodSign("findById@2_int_java.lang.String");
//            request.setArgs(new Object[]{10, "kn"});
//            final RpcResponse user = providerInvoker.invoke(request);
//            System.out.println("user return : " + user.getData());

//            System.out.println("Cast 19. >>===[provider 端流控测试]===");
//            for (int i = 0; i < 100; i++) {
//                try {
//                    Thread.sleep(1000);
//                    final RpcResponse<Object> result = transport.invoke(request);
//                    System.out.println(i + " *** result = " + result.getData());
//                } catch (Exception e) {
//                    System.out.println(i + " --- ex = " + e.getMessage());
//                }
//            }

        };
    }

}
