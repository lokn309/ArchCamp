package cn.lokn.knrpc.core.api;

import cn.lokn.knrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 重构，用于封装参数
 * @author: lokn
 * @date: 2024/03/17 23:35
 */
@Data
public class RpcContext {

    List<Filter> filters;

    Router<InstanceMeta> router;

    LoadBalancer<InstanceMeta> loadBalancer;

    private Map<String, String> parameters = new HashMap<>();

    // 实现从 consumer 传参数到 provider 端
    public static ThreadLocal<Map<String, String>> contextParams = new ThreadLocal<>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    public static void setContextParams(String key, String value) {
        contextParams.get().put(key, value);
    }

    public static String getContextParams(String key) {
        return contextParams.get().get(key);
    }

    public static void removeContextParams(String key) {
        contextParams.get().remove(key);
    }


}
