package cn.lokn.knrpc.core.provider;

import cn.lokn.knrpc.core.annotation.KNProvider;
import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.config.AppConfigProperties;
import cn.lokn.knrpc.core.config.ProviderConfigProperties;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.meta.ProviderMeta;
import cn.lokn.knrpc.core.meta.ServiceMeta;
import cn.lokn.knrpc.core.util.MethodUtils;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * @description: 服务提供者的启动类
 * @author: lokn
 * @date: 2024/03/07 23:27
 */
@Data
@Slf4j
public class ProviderBoostrap implements ApplicationContextAware {

    /**
     * 当 {@link ProviderBoostrap } 被丢到 spring 容器后，applicationContext 会被自动加载到容器中
     * 注意：属性名称需要用 applicationContext
     */
    ApplicationContext applicationContext; // 获取所有的bean

    RegistryCenter rc;

    private InstanceMeta instance;

    private String port;

    private AppConfigProperties appConfigProperties;

    private ProviderConfigProperties providerConfigProperties;

    /**
     * 获取所有的provider
     * 统一将方法签名解析后放到 skeleton 桩子中，避免每次都解析请求参数，提升性能
     */
    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    public ProviderBoostrap(String port, AppConfigProperties appConfigProperties, ProviderConfigProperties providerConfigProperties) {
        this.port = port;
        this.appConfigProperties = appConfigProperties;
        this.providerConfigProperties = providerConfigProperties;
    }

    @PostConstruct
    public void init() {
        // 获取所有加了自定义 @KNProvider 注解的bean
        final Map<String, Object> providers = applicationContext.getBeansWithAnnotation(KNProvider.class);
        rc = applicationContext.getBean(RegistryCenter.class);
        providers.forEach((x, y) -> log.info(x));
        providers.values().forEach(this::genInterface);
    }

    /**
     * 获取所有标注了 {@link KNProvider}  实现类的接口元数据信息，
     * 并将构建的接口元数据信息 {@link ProviderMeta} 保存到 skeleton 桩子中
     *
     * @param impl 标注 {@link KNProvider} 注解的实现类
     */
    private void genInterface(Object impl) {
        Arrays.stream(impl.getClass().getInterfaces()).forEach(service -> {
            final Method[] methods = service.getMethods();
            for (Method method : methods) {
                // 排除 Object 类中的方法。eg： toString() etc.
                if (MethodUtils.checkLocalMethod(method)) {
                    continue;
                }
                createProvider(service, impl, method);
            }
        });
    }

    /**
     * 构建生产者元数据信息 {@link ProviderMeta}，并将生产者元数据保存到 skeleton 桩子中
     *
     * @param service   接口全限定路径
     * @param impl      实现类对象
     * @param method    方法
     */
    private void createProvider(Class<?> service, Object impl, Method method) {
        ProviderMeta providerMeta = ProviderMeta.builder()
                .serviceImpl(impl)
                .method(method)
                .methodSign(MethodUtils.methodSign(method))
                .build();
        log.info(" create a provider : " + providerMeta);
        skeleton.add(service.getCanonicalName(), providerMeta);
    }

    /**
     * 延迟服务暴露，只有当spring bean 完成后才向zk注册
     */
    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = InstanceMeta.http(ip, Integer.valueOf(port));
        rc.start();
        skeleton.keySet().forEach(this::registerService);
    }

    /**
     * 向 zk 注册生产者信息
     *
     * @param service 生产者信息
     */
    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app(appConfigProperties.getId())
                .namespace(appConfigProperties.getNamespace())
                .env(appConfigProperties.getEnv())
                .build();
        // 注册时，向注册中心带入灰度信息
        instance.getParameters().putAll(providerConfigProperties.getMetas());
        rc.register(serviceMeta, instance);
    }

    @PreDestroy
    public void stop() {
        skeleton.keySet().forEach(this::unRegisterService);
        rc.stop();
    }

    private void unRegisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app(appConfigProperties.getId())
                .namespace(appConfigProperties.getNamespace())
                .env(appConfigProperties.getEnv())
                .build();
        rc.unregister(serviceMeta, instance);
    }

}
