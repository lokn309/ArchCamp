package cn.lokn.knrpc.core.api;

import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.meta.ServiceMeta;
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
    void register(ServiceMeta service, InstanceMeta instance);

    /**
     * provider 使用
     *
     * @param service
     * @param instance
     */
    void unregister(ServiceMeta service, InstanceMeta instance);

    /**
     * consumer 侧
     */
    List<InstanceMeta> fetchAll(ServiceMeta service);

    /**
     * consumer 侧
     *
     * @param listener 用来修改外部的数据结构
     */
    void subscribe(ServiceMeta service, ChangedListener listener);

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
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangedListener listener) {

        }
    }
}
