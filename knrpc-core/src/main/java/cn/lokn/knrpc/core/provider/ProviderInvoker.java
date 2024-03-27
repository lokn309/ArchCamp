package cn.lokn.knrpc.core.provider;

import cn.lokn.knrpc.core.api.RpcException;
import cn.lokn.knrpc.core.api.RpcRequest;
import cn.lokn.knrpc.core.api.RpcResponse;
import cn.lokn.knrpc.core.meta.ProviderMeta;
import cn.lokn.knrpc.core.util.MethodUtils;
import cn.lokn.knrpc.core.util.TypeUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/22 23:43
 */
public class ProviderInvoker {

    // 获取所有的provider
    // 同一将方法签名解析后放到 skeleton 桩子中，避免每次都解析请求参数
    private MultiValueMap<String, ProviderMeta> skeleton;

    public ProviderInvoker(ProviderBoostrap providerBoostrap) {
        this.skeleton = providerBoostrap.getSkeleton();
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        if (MethodUtils.checkLocalMethod(request.getMethodSign())) {
            return null;
        }
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        RpcResponse<Object> rpcResponse = new RpcResponse<>();
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
            Method method = meta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            final Object result = method.invoke(meta.getServiceImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
            return rpcResponse;
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RpcException(e.getTargetException().getMessage()));
        } catch (Exception e) {
            rpcResponse.setEx(new RpcException(e.getMessage()));
        }
        return rpcResponse;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if (args == null || args.length == 0) return args;
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return result;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        final Optional<ProviderMeta> result = providerMetas.stream().filter(meta -> meta.getMethodSign().equals(methodSign)).findFirst();
        return result.orElse(null);
    }

}
