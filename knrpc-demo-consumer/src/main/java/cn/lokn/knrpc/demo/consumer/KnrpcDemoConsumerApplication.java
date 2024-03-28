package cn.lokn.knrpc.demo.consumer;

import cn.lokn.knrpc.core.annotation.KNConsumer;
import cn.lokn.knrpc.core.consumer.ConsumerConfig;
import cn.lokn.knrpc.demo.api.User;
import cn.lokn.knrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping("/find")
    public User find(@RequestParam("timeout") int timeout) {
        return userService.find(timeout);
    }

    public static void main(String[] args) {
        SpringApplication.run(KnrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return x -> {
            long start = System.currentTimeMillis();
            userService.find(100);
            System.out.println("userService.find task "
                    + (System.currentTimeMillis() - start) + " ms");
            // testAll();
        };
    }

    private void testAll() {
        System.out.println("Cast 1. >>===[测试请求参数为 int，返回值为 User 对象]===");
        System.out.println(userService.findById(1));

        System.out.println("Cast 2. >>===[测试请求参数为 int 和 String，返回值为 User 对象]===");
        System.out.println(userService.findById(2, "cast2"));

        System.out.println("Cast 3. >>===[测试请求参数为 long，返回值为 long ]===");
        System.out.println(userService.getId(3L));

        System.out.println("Cast 4. >>===[测试请求参数为 User 对象，返回值为 long ]===");
        System.out.println(userService.getId(new User(4, "cast4")));

        System.out.println("Cast 5. >>===[测试请求参数为 float，返回值 long 值]===");
        System.out.println(userService.getId(2.4f));

        System.out.println("Cast 6. >>===[测试请求参数为 void ，返回值 String 值]===");
        System.out.println(userService.getName());

        System.out.println("Cast 7. >>===[测试请求参数为 void，返回值 String 值]===");
        System.out.println(userService.getName());

        System.out.println("Cast 8. >>===[测试请求参数为 int，返回值为 String 对象]===");
        System.out.println(userService.getName(8));

        System.out.println("Cast 9. >>===[测试请求参数为 void，返回值为 int[] 数组]===");
        System.out.println(Arrays.toString(userService.getIds()));

        System.out.println("Cast 10. >>===[测试请求参数为 void，返回值为 long[] 数组 ]===");
        System.out.println(Arrays.toString(userService.getLongIds()));

        System.out.println("Cast 11. >>===[测试请求参数为 int[]，返回值为 int[] 数组 ]===");
        System.out.println(Arrays.toString(userService.getIds(new int[]{11, 12, 13, 14})));

        System.out.println("Cast 12. >>===[测试请求参数为 User[]，返回值为 User[] 数组 ]===");
        User[] users = new User[]{
                new User(12, "cast12-1"),
                new User(13, "cast12-2"),
        };
        System.out.println(Arrays.toString(userService.findUsers(users)));

        System.out.println("Cast 13. >>===[测试请求参数为 List<User>，返回值为 List<User> 集合 ]===");
        final List<User> list = Arrays.asList(
                new User(13, "cast13-1"),
                new User(14, "cast13-2")
        );
        System.out.println(userService.getList(list));

        System.out.println("Cast 14. >>===[测试请求参数为 Map<String, User>，返回值为 Map<String, User> 数组 ]===");
        final Map<String, User> userMap = new HashMap<>();
        userMap.put("cast14-1", new User(14, "cast14-1"));
        userMap.put("cast14-2", new User(15, "cast14-2"));

        System.out.println(userService.getMap(userMap));

        System.out.println("Cast 15. >>===[测试请求参数为 boolean，返回值为 Boolean 对象]===");
        System.out.println(userService.getFlag(Boolean.TRUE));

        System.out.println("Cast 16. >>===[测试请求参数为 long，返回值为 User 对象]===");
        System.out.println(userService.findById(16L));

        System.out.println("Cast 17. >>===[测试请求参数为 boolean，返回值为 User 对象]===");
        System.out.println(userService.ex(Boolean.TRUE));
    }

}
