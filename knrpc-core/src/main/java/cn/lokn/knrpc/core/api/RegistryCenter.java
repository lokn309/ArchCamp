package cn.lokn.knrpc.core.api;

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
    void register(String service, String instance);

    /**
     * provider 使用
     *
     * @param service
     * @param instance
     */
    void unregister(String service, String instance);

    /**
     * consumer 侧
     */
    List<String> fetchAll(String service);

    /**
     * consumer 侧
     */
//    void subscribe();

//        void heartbeat()

    class StaticRegistryCenter implements RegistryCenter {

        List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

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
            return providers;
        }
    }
}
