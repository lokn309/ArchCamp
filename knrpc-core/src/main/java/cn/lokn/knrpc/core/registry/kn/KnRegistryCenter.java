package cn.lokn.knrpc.core.registry.kn;

import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.consumer.HttpInvoker;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.meta.ServiceMeta;
import cn.lokn.knrpc.core.registry.ChangedListener;
import cn.lokn.knrpc.core.registry.Event;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description: implementation for kn registry center
 * @author: lokn
 * @date: 2024/05/03 08:02
 */
@Slf4j
public class KnRegistryCenter implements RegistryCenter {

    @Value("${knregistry.services}")
    private String servers;

    @Override
    public void start() {
        log.info(" ====>>> [KnRegistry] : start with server: {}", servers);
        executor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void stop() {
        log.info(" ====>>> [KnRegistry] : stop with server: {}", servers);
        executor.shutdown();
        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>> [KnRegistry] : registry instance {} for {}", instance, servers);
        HttpInvoker.httpPost(servers + "/reg?service=" + service.toPath(), JSON.toJSONString(instance), InstanceMeta.class);
        log.info(" ====>>> [KnRegistry] : registered {} ", instance);

    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>> [KnRegistry] : unregistry instance {} for {}", instance, servers);
        HttpInvoker.httpPost(servers + "/unreg?service=" + service.toPath(), JSON.toJSONString(instance), InstanceMeta.class);
        log.info(" ====>>> [KnRegistry] : unregistered {} ", instance);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ====>>> [KnRegistry] : find all instance for {}", servers);
        final List<InstanceMeta> instances = HttpInvoker.httpGet(servers + "/findAll?service=" + service.toPath(),
                new TypeReference<List<InstanceMeta>>() {
                });
        log.info(" ====>>> [KnRegistry] : find all = {} ", instances);
        return null;
    }

    Map<String, Long> VERSIONS = new HashMap<>();

    ScheduledExecutorService executor = null;

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        executor.scheduleWithFixedDelay(() -> {
            final Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            final Long newVersion = HttpInvoker.httpGet(servers + "version?service=" + service.toPath(), Long.class);
            log.info(" ===>>> [KnRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (newVersion > version) {
                final List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }
}
