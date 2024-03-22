package cn.lokn.knrpc.core.consumer;

import cn.lokn.knrpc.core.annotation.KNConsumer;
import cn.lokn.knrpc.core.api.LoadBalancer;
import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.api.Router;
import cn.lokn.knrpc.core.api.RpcContext;
import cn.lokn.knrpc.core.util.MethodUtils;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 消费端启动类
 * @author: lokn
 * @date: 2024/03/10 19:47
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;
    Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);

        // 这里有一个技巧，利用 applicationRunner 让所有的 Bean 初始化完后，在进行 Bean 实例的获取
        final String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            final Object bean = applicationContext.getBean(name);
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), KNConsumer.class);

            fields.forEach(f -> {
                try {
                    // 先拿类型
                    final Class<?> service = f.getType();
                    // 获取全限定类路径
                    final String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        // 创建代理对象
                        consumer = createFromRegistry(service, context, rc);
                    }
                    // 可见行变为true，不然反射时，会失败
                    f.setAccessible(true);
                    f.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });

        }

    }

    private Object createFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        String serverName = service.getCanonicalName();
        final List<String> providers = mapUrl(rc.fetchAll(serverName));
        System.out.println(" ===> map to providers:");
        providers.forEach(System.out::println);

        // 订阅 zk 的变化
        rc.subscribe(serverName, event -> {
            providers.clear();
            providers.addAll(mapUrl(event.getData()));
        });

        return createConsumer(service, context, providers);
    }

    private List<String> mapUrl(List<String> nodes) {
        return nodes.stream().map(x -> "http://" + x.replace("_", ":"))
                .collect(Collectors.toList());
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        // 动态代理实现
        return Proxy.newProxyInstance(
                service.getClassLoader(),
                new Class[]{service},
                new KNInvocationHandler(service, context, providers));
    }

}
