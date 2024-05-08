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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: implementation for kn registry center
 * @author: lokn
 * @date: 2024/05/03 08:02
 */
@Slf4j
public class KnRegistryCenter implements RegistryCenter {

    private static final String REG_PATH = "/reg";
    private static final String UNREG_PATH = "/unreg";
    private static final String FINDALL_PATH = "/findAll";
    private static final String VERSION_PATH = "/version";
    private static final String RENEWS_PATH = "/renews";

    @Value("${knregistry.servers}")
    private String servers;

    Map<String, Long> VERSIONS = new HashMap<>();
    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();

    KnHealthChecker healthChecker = new KnHealthChecker();

    @Override
    public void start() {
        log.info(" ====>>> [KnRegistry] : start with server: {}", servers);
        healthChecker.start();
        providerCheck();
    }

    @Override
    public void stop() {
        log.info(" ====>>> [KnRegistry] : stop with server: {}", servers);
        healthChecker.stop();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>> [KnRegistry] : registry instance {} for {}", instance, servers);
        HttpInvoker.httpPost(regPath(service), JSON.toJSONString(instance), InstanceMeta.class);
        log.info(" ====>>> [KnRegistry] : registered {} ", instance);
        RENEWS.add(instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>> [KnRegistry] : unregister instance {} for {}", instance, servers);
        HttpInvoker.httpPost(unregPath(service), JSON.toJSONString(instance), InstanceMeta.class);
        log.info(" ====>>> [KnRegistry] : unregistered {} ", instance);
        RENEWS.remove(instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ====>>> [KnRegistry] : find all instance for {}", servers);
        final List<InstanceMeta> instances = HttpInvoker.httpGet(findAllPath(service),
                new TypeReference<List<InstanceMeta>>() {});
        log.info(" ====>>> [KnRegistry] : findAll = {} ", instances);
        return instances;
    }

    public void providerCheck() {
        healthChecker.providerCheck(() -> {
            RENEWS.keySet().forEach(instance -> {
                Long timestamp = HttpInvoker.httpPost(renewsPath(RENEWS.get(instance)),
                        JSON.toJSONString(instance), Long.class);
                log.info(" ====>>> [KnRegistry] : renew instance {} at {}", instance, timestamp);
            });
        });
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        healthChecker.consumerCheck(() -> {
            final Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            final Long newVersion = HttpInvoker.httpGet(versionPath(service), Long.class);
            log.info(" ===>>> [KnRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (newVersion > version) {
                final List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                // 此处的位置是为了避免 fire 处理报错，导致VERSIONS版本多次更新的问题
                VERSIONS.put(service.toPath(), newVersion);
            }
        });
    }

    private String regPath(ServiceMeta service) {
        return path(REG_PATH, service);
    }

    private String unregPath(ServiceMeta service) {
        return path(UNREG_PATH, service);
    }

    private String findAllPath(ServiceMeta service) {
        return path(FINDALL_PATH, service);
    }

    private String versionPath(ServiceMeta service) {
        return path(VERSION_PATH, service);
    }

    private String renewsPath(List<ServiceMeta> serviceList) {
        return path(RENEWS_PATH, serviceList);
    }

    private String path(String context, ServiceMeta service) {
        return servers + context + "?service=" + service.toPath();
    }

    private String path(String context, List<ServiceMeta> serviceList) {
        StringBuffer sb = new StringBuffer();
        for (ServiceMeta service : serviceList) {
            sb.append(service.toPath()).append(",");
        }
        String services = sb.toString();
        if (services.endsWith(",")) services = services.substring(0, services.length() - 1);
        log.info(" ====>>> [KnRegistry] : renew instance for {}", services);
        return servers + context + "?services=" + services;
    }

}
