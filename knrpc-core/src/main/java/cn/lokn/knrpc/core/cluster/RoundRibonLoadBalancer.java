package cn.lokn.knrpc.core.cluster;

import cn.lokn.knrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/17 22:52
 */
public class RoundRibonLoadBalancer<T> implements LoadBalancer<T> {

    AtomicInteger index = new AtomicInteger(0);

    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.isEmpty()) {
            return null;
        }
        if (providers.size() == 1)  return providers.get(0);
        // & 0x7ffffff 是为了避免溢出
        return providers.get((index.getAndIncrement() & 0x7fffffff) % providers.size());
    }
}
