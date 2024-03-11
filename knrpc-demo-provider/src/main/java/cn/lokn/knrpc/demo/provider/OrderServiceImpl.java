package cn.lokn.knrpc.demo.provider;

import cn.lokn.knrpc.core.annotation.KNProvider;
import cn.lokn.knrpc.demo.api.Order;
import cn.lokn.knrpc.demo.api.OrderService;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/11 23:14
 */
@Component
@KNProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {
        if (id == 404) {
            throw new RuntimeException("404 Exception");
        }
        return new Order(id, 5.6f);
    }
}
