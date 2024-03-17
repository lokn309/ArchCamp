package cn.lokn.knrpc.demo.consumer;

import cn.lokn.knrpc.core.annotation.KNConsumer;
import cn.lokn.knrpc.core.consumer.ConsumerConfig;
import cn.lokn.knrpc.demo.api.Order;
import cn.lokn.knrpc.demo.api.OrderService;
import cn.lokn.knrpc.demo.api.User;
import cn.lokn.knrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@SpringBootApplication
@RestController
@Import({ConsumerConfig.class})
public class KnrpcDemoConsumerApplication {

    @KNConsumer
    UserService userService;

//    @KNConsumer
//    OrderService orderService;

    @RequestMapping("/")
    public User invoke(int id) {
        return userService.findById(id);
    }

    public static void main(String[] args) {
        SpringApplication.run(KnrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return x -> {
//            User user = userService.findById(1);
//            System.out.println(" ===> user = " + user);

//            final Order order = orderService.findById(20);
//            System.out.println(" ===> order = " + order);

//            final Order order404 = orderService.findById(404);
//            System.out.println(" ===> order404 = " + order404);

//            final String string = orderService.toString();
//            System.out.println(" ===> orderString = " + string);

            final User byId = userService.findById(20, "张飞");
            System.out.println(" ===> user$findById(id, name) = " + byId);
            System.out.println("-------------------------");
            final User byId1 = userService.findById(30);
            System.out.println(" ===> user$findById(id) = " + byId1);

            System.out.println(" ===> getName(id)" + userService.getName(12));

            System.out.println(" ===> getId(long) " + userService.getId(10));

            System.out.println(Arrays.toString(userService.getIds()));
            System.out.println(Arrays.toString(userService.getIds(new int[]{12, 23, 34})));

        };
    }

}
