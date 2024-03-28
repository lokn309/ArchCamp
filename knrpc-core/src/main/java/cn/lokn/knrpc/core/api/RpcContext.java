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

}
