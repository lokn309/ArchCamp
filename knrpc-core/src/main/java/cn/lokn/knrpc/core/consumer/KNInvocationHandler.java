package cn.lokn.knrpc.core.consumer;

import cn.lokn.knrpc.core.api.*;
import cn.lokn.knrpc.core.consumer.http.OkHttpInvoker;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.util.MethodUtils;
import cn.lokn.knrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;


/**
 * @description: 消费端动态代理处理类
 * @author: lokn
 * @date: 2024/03/10 23:03
 */
@Slf4j
public class KNInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext context;
    List<InstanceMeta> providers;

    HttpInvoker httpInvoker;

    public KNInvocationHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
        int timeout = Integer.parseInt(context.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }

        RpcRequest request = new RpcRequest();
        request.setService(service.getCanonicalName());
        request.setMethodSign(MethodUtils.methodSign(method));
        request.setArgs(args);

        int retries = Integer.parseInt(context.getParameters()
                .getOrDefault("app.reties", "1"));

        while (retries-- > 0) {
            log.debug(" ===> retries: " + retries);
            try {
                for (Filter filter : this.context.getFilters()) {
                    final Object cacheResult = filter.prefilter(request);
                    if (cacheResult != null) {
                        log.info("{} ===> prefilter: {}", request, cacheResult);
                        return cacheResult;
                    }
                }

                final List<InstanceMeta> nodes = context.getRouter().route(providers);
                InstanceMeta instance = context.getLoadBalancer().choose(nodes);
                log.info("loadBalancer.choose(urls) == " + instance);

                RpcResponse<?> rpcResponse = httpInvoker.post(request, instance.getUrl());
                final Object result = castReturnResult(method, rpcResponse);

                for (Filter filter : this.context.getFilters()) {
                    Object filterResult = filter.postfilter(request, rpcResponse, result);
                    if (filterResult != null) {
                        return filterResult;
                    }
                }
                return result;
            } catch (Exception ex) {
                if (!(ex.getCause() instanceof SocketTimeoutException)) {
                    throw ex;
                }
            }
        }
        return null;
    }

    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            Exception responseEx = rpcResponse.getEx();
            if (responseEx instanceof RpcException ex) {
                throw ex;
            }
            throw new RpcException(responseEx, RpcException.NoSuchMethodEx);
        }
    }

}
