package cn.lokn.knrpc.demo.provider;

import cn.lokn.knrpc.core.annotation.KNProvider;
import cn.lokn.knrpc.demo.api.User;
import cn.lokn.knrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * @description: 服务提供者的实现
 * @author: lokn
 * @date: 2024/03/06 23:55
 */
@Component
@KNProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(int id) {
        return new User(id, "KN-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, name);
    }

    @Override
    public long getId(long id) {
        return id;
    }

    @Override
    public int getId(User user) {
        return user.getId();
    }

    @Override
    public String getName() {
        return null;
    }
}
