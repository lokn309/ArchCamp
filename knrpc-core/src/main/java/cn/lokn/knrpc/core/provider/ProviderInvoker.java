package cn.lokn.knrpc.core.provider;

import cn.lokn.knrpc.core.api.RpcContext;
import cn.lokn.knrpc.core.api.RpcException;
import cn.lokn.knrpc.core.api.RpcRequest;
import cn.lokn.knrpc.core.api.RpcResponse;
import cn.lokn.knrpc.core.governance.SlidingTimeWindow;
import cn.lokn.knrpc.core.meta.ProviderMeta;
import cn.lokn.knrpc.core.util.MethodUtils;
import cn.lokn.knrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/22 23:43
 */
@Slf4j
public class ProviderInvoker {

    // 获取所有的provider
    // 同一将方法签名解析后放到 skeleton 桩子中，避免每次都解析请求参数
    private MultiValueMap<String, ProviderMeta> skeleton;

    /**
     * 流量控制默认为 20
     * 当流控值为 -1 时，不进行流量控制
     */
    private final int trafficControl;
    // todo 1201 : 改成map，针对不同的服务用不同的流控值
    // todo 1202 : 对多个节点是共享一个数值，，，把这个map放到redis

    final Map<String, SlidingTimeWindow> windows = new HashMap<>();
    final Map<String, String> metas;

    public ProviderInvoker(ProviderBoostrap providerBoostrap) {
        this.skeleton = providerBoostrap.getSkeleton();
        this.metas = providerBoostrap.getProviderProperties().getMetas();
        this.trafficControl = Integer.parseInt(metas.getOrDefault("tc", "20"));
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        if (MethodUtils.checkLocalMethod(request.getMethodSign())) {
            return null;
        }
        if (!request.getParams().isEmpty()) {
            request.getParams().forEach(RpcContext::setContextParams);
        }

        String service = request.getService();
        // 流控
        if (trafficControl > 0) {
            trafficControl(service);
        }

        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        RpcResponse<Object> rpcResponse = new RpcResponse<>();
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
            Method method = meta.getMethod();
            // todo 处理 list 泛型bug
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            final Object result = method.invoke(meta.getServiceImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
            return rpcResponse;
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RpcException(e.getTargetException().getMessage()));
        } catch (Exception e) {
            rpcResponse.setEx(new RpcException(e.getMessage()));
        } finally {
            // 防止内存泄露和上下文污染
            RpcContext.contextParams.get().clear();
        }
        return rpcResponse;
    }

    private void trafficControl(String service) {
        synchronized (windows) {
            final SlidingTimeWindow window = windows.computeIfAbsent(service, x -> new SlidingTimeWindow());
            if (window.calcSum() >= trafficControl) {
                System.out.println(window);
                throw new RpcException("service " + service + " invoker in 30s/[" + window.getSum()
                        + "] larger than tpsLimit = " + trafficControl);
            }

            window.record(System.currentTimeMillis());
            log.debug("service {} in window with {}", service, window.getSum());
        }
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
