package cn.lokn.knrpc.core.cluster;

import cn.lokn.knrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/17 22:52
 */
public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.isEmpty()) {
            return null;
        }
        if (providers.size() == 1)  return providers.get(0);
        return providers.get(random.nextInt(providers.size()));
    }
}
