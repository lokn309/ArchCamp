package cn.lokn.knrpc.core.api;

import java.util.List;

/**
 * @description: 路由器
 * @author: lokn
 * @date: 2024/03/17 21:16
 */
public interface Router<T> {

    List<T> route(List<T> providers);

    Router Default = p -> p;

}
