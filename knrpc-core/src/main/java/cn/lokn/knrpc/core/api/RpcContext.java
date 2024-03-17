package cn.lokn.knrpc.core.api;

import lombok.Data;

import java.util.List;

/**
 * @description: 重构，用于封装参数
 * @author: lokn
 * @date: 2024/03/17 23:35
 */
@Data
public class RpcContext {

    List<Filter> filters; // todo

    Router router;

    LoadBalancer loadBalancer;

}
