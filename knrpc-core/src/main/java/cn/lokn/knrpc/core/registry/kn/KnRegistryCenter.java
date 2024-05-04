package cn.lokn.knrpc.core.registry.kn;

import cn.lokn.knrpc.core.api.RegistryCenter;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.meta.ServiceMeta;
import cn.lokn.knrpc.core.registry.ChangedListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

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
    }

    @Override
    public void stop() {
        log.info(" ====>>> [KnRegistry] : stop with server: {}", servers);

    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>> [KnRegistry] : registry instance {} for {}", instance, servers);

    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {

    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        return null;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {

    }
}
