package cn.lokn.knrpc.core.filter;

import cn.lokn.knrpc.core.api.Filter;
import cn.lokn.knrpc.core.api.RpcContext;
import cn.lokn.knrpc.core.api.RpcRequest;
import cn.lokn.knrpc.core.api.RpcResponse;

import java.util.Map;

/**
 * @description:
 * @author: lokn
 * @date: 2024/04/05 15:51
 */
public class ContextParamsFilter implements Filter {
    @Override
    public Object prefilter(RpcRequest request) {
        final Map<String, String> params = RpcContext.contextParams.get();
        if (params != null && !params.isEmpty()) {
            request.getParams().putAll(params);
        }
        return null;
    }

    @Override
    public Object postfilter(RpcRequest request, RpcResponse response, Object result) {

        return null;
    }
}
