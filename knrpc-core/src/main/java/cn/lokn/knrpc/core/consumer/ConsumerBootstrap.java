package cn.lokn.knrpc.core.consumer;

import cn.lokn.knrpc.core.annotation.KNConsumer;
import cn.lokn.knrpc.core.api.*;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.meta.ServiceMeta;
import cn.lokn.knrpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 消费端启动类
 * @author: lokn
 * @date: 2024/03/10 19:47
 */
@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;
    Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;

    @Value("${app.reties}")
    private int reties;

    @Value("${app.timeout}")
    private int timeout;

    public void start() {
        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        List<Filter> filters = applicationContext.getBeansOfType(Filter.class).values().stream().toList();

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);
        context.getParameters().put("app.reties", String.valueOf(reties));
        context.getParameters().put("app.timeout", String.valueOf(timeout));

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
                        stub.put(serviceName, consumer);
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
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service.getCanonicalName())
                .build();
        final List<InstanceMeta> providers = rc.fetchAll(serviceMeta);
        log.info(" ===> map to providers:");
        providers.forEach(System.out::println);

        // 订阅 zk 的变化
        rc.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });

        return createConsumer(service, context, providers);
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        // 动态代理实现
        return Proxy.newProxyInstance(
                service.getClassLoader(),
                new Class[]{service},
                new KNInvocationHandler(service, context, providers));
    }

}
