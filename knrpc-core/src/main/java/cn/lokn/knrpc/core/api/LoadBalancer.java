package cn.lokn.knrpc.core.api;

import java.util.List;

/**
 * @description: 负载均衡
 *                  weightedRR（权重）、AAWR-自适应、
 * @author: lokn
 * @date: 2024/03/17 21:16
 */
public interface LoadBalancer<T> {

    T choose(List<T> providers);

    LoadBalancer Default = p -> (p == null || p.size() == 0) ? null : p.get(0);

}