package cn.lokn.knrpc.core.consumer;

import cn.lokn.knrpc.core.api.RpcRequest;
import cn.lokn.knrpc.core.api.RpcResponse;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/23 00:09
 */
public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest rpcRequest, String url);

}
