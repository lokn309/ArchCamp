package cn.lokn.knrpc.core.api;

import java.util.List;

/**
 * @description: 过滤器
 * @author: lokn
 * @date: 2024/03/17 21:16
 */
public interface Filter {

    Object prefilter(RpcRequest request);

    Object postfilter(RpcRequest request, RpcResponse response, Object result);

    // 挡板排查问题 Mock 方法
    // A -> B -> C 有问题
    // - -> - -> D 还有问题
    // - -> D

    Filter Default = new Filter() {
        @Override
        public RpcResponse prefilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
            return null;
        }
    };

}
