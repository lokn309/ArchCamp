package cn.lokn.knrpc.core.transport;

import cn.lokn.knrpc.core.api.RpcRequest;
import cn.lokn.knrpc.core.api.RpcResponse;
import cn.lokn.knrpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: lokn
 * @date: 2024/04/05 19:46
 */
@RestController
public class SpringBootTransport {

    @Autowired
    ProviderInvoker providerInvoker;

    // 使用 http + json 来实现序列化和通信
    @RequestMapping("/rpc")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }

}
