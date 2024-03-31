package cn.lokn.knrpc.core.consumer;

import cn.lokn.knrpc.core.api.*;
import cn.lokn.knrpc.core.consumer.http.OkHttpInvoker;
import cn.lokn.knrpc.core.governance.SlidingTimeWindow;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import cn.lokn.knrpc.core.util.MethodUtils;
import cn.lokn.knrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @description: 消费端动态代理处理类
 * @author: lokn
 * @date: 2024/03/10 23:03
 */
@Slf4j
public class KNInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext context;
    final List<InstanceMeta> providers;
    List<InstanceMeta> isolatedProviders = new ArrayList<>();

    final List<InstanceMeta> halfOpenProviders = new ArrayList<>();

    Map<String, SlidingTimeWindow> windows = new HashMap<>();

    HttpInvoker httpInvoker;

    ScheduledExecutorService executor;

    public KNInvocationHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
        int timeout = Integer.parseInt(context.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
        this.executor = Executors.newScheduledThreadPool(1);
        this.executor.scheduleWithFixedDelay(this::halfOpen, 10, 60, TimeUnit.SECONDS);
    }

    private void halfOpen() {
        log.debug(" ===> half open isolatedProviders: {}", isolatedProviders);
        halfOpenProviders.clear();
        halfOpenProviders.addAll(isolatedProviders);
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

                InstanceMeta instance;
                synchronized (halfOpenProviders) {
                    if (halfOpenProviders.isEmpty()) {
                        List<InstanceMeta> nodes = context.getRouter().route(providers);
                        instance = context.getLoadBalancer().choose(nodes);
                        log.info(" loadBalancer.choose(urls) == " + instance);
                    } else {
                        instance = halfOpenProviders.remove(0);
                        log.debug(" check alive instance ===> {}", instance);
                    }
                }

                RpcResponse<?> rpcResponse = null;
                Object result = null;

                String url = instance.getUrl();
                try {
                    rpcResponse = httpInvoker.post(request, instance.getUrl());
                    result = castReturnResult(method, rpcResponse);
                } catch (Exception e) {
                    // 故障的规则统计和隔离
                    // 每一次异常，记录一次，统计30s的异常数
                    SlidingTimeWindow window = windows.get(url);
                    if (window == null) {
                        window = new SlidingTimeWindow();
                        windows.put(url, window);
                    }

                    window.record(System.currentTimeMillis());
                    log.debug("instance {} in window with {}", url, window.getSum());
                    // 发生了10次，就做故障
                    if (window.getSum() >= 10) {
                        isolate(instance);
                    }

                    throw e;
                }

                // 探活
                synchronized (providers) {
                    if (!providers.contains(instance)) {
                        isolatedProviders.remove(instance);
                        providers.add(instance);
                        log.debug(" ===> instance {} is recovered. isolatedProviders={}, providers={}", instance, isolatedProviders, providers);
                    }
                }

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

    private void isolate(InstanceMeta instance) {
        log.debug(" ===> isolate instance: " + instance);
        providers.remove(instance);
        log.debug(" ===> providers = {}", providers);
        isolatedProviders.add(instance);
        log.debug(" ===> isolateProviders = {}", isolatedProviders);
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
