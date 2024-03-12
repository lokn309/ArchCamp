package cn.lokn.knrpc.core.consumer;

import cn.lokn.knrpc.core.annotation.KNConsumer;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/10 19:47
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        // 这里有一个技巧，利用 applicationRunner 让所有的 Bean 初始化完后，在进行 Bean 实例的获取
        final String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            final Object bean = applicationContext.getBean(name);
            List<Field> fields = findAnnotatedField(bean.getClass());

            fields.stream().forEach(f -> {
                try {
                    // 先拿类型
                    final Class<?> service = f.getType();
                    // 获取全限定类路径
                    final String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        // 创建代理对象
                        consumer = createConsumer(service);
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

    private Object createConsumer(Class<?> service) {
        // 动态代理实现
        return Proxy.newProxyInstance(
                service.getClassLoader(),
                new Class[]{service},
                new KNInvocationHandler(service));
    }

    /**
     * 获取所有带有注解 {@link KNConsumer} 的属性
     */
    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> result = new ArrayList<>();
        // 注意此处需要获取到父类，所以此处采用 while 循环获取
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(KNConsumer.class)) {
                    result.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }

}
