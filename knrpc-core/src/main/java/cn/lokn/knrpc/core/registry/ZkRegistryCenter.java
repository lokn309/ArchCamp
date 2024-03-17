package cn.lokn.knrpc.core.registry;

import cn.lokn.knrpc.core.api.RegistryCenter;

import java.util.List;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/18 00:21
 */
public class ZkRegistryCenter implements RegistryCenter {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void register(String service, String instance) {

    }

    @Override
    public void unregister(String service, String instance) {

    }

    @Override
    public List<String> fetchAll(String service) {
        return null;
    }
}
