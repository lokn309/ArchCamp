package cn.lokn.knrpc.core.api;

import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.registry.ChangedListener;

import java.util.List;

public interface RegistryCenter {

    /**
     * provider 和 consumer 使用
     */
    void start();

    /**
     * provider 和 consumer 使用
     */
    void stop();

    /**
     * provider 使用
     *
     * @param service
     * @param instance
     */
    void register(String service, InstanceMeta instance);

    /**
     * provider 使用
     *
     * @param service
     * @param instance
     */
    void unregister(String service, InstanceMeta instance);

    /**
     * consumer 侧
     */
    List<InstanceMeta> fetchAll(String service);

    /**
     * consumer 侧
     *
     * @param listener 用来修改外部的数据结构
     */
    void subscribe(String service, ChangedListener listener);

//        void heartbeat()

    class StaticRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, InstanceMeta instance) {

        }

        @Override
        public void unregister(String service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {

        }
    }
}
