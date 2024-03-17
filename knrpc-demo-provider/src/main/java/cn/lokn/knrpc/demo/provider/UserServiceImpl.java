package cn.lokn.knrpc.demo.provider;

import cn.lokn.knrpc.core.annotation.KNProvider;
import cn.lokn.knrpc.demo.api.User;
import cn.lokn.knrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 服务提供者的实现
 * @author: lokn
 * @date: 2024/03/06 23:55
 */
@Component
@KNProvider
public class UserServiceImpl implements UserService {

    @Autowired
    Environment environment;

    @Override
    public User findById(int id) {
        return new User(id, "KN-"
                + environment.getProperty("server.port")
                + "_" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, "KN-" + name + "_" + System.currentTimeMillis());
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
        return "getName";
    }

    @Override
    public String getName(int id) {
        return "getName = " + id;
    }

    @Override
    public int[] getIds() {
        return new int[]{0 ,2, 4};
    }

    @Override
    public int[] getIds(int[] ids) {
        return ids;
    }

    @Override
    public long[] getLongIds() {
        return new long[]{1L, 3L, 5L};
    }

    @Override
    public List<User> getLists() {
        return new ArrayList<>();
    }


}
