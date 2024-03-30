package cn.lokn.knrpc.core.filter;

import cn.lokn.knrpc.core.api.Filter;
import cn.lokn.knrpc.core.api.RpcRequest;
import cn.lokn.knrpc.core.api.RpcResponse;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @description: 缓存过滤器
 * @author: lokn
 * @date: 2024/03/24 15:10
 */
public class CacheFilter implements Filter {

    // TODO 优化： 替换成 guava cache，加容量和过期时间
    static Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public Object prefilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
