package cn.lokn.knrpc.demo.provider;

import cn.lokn.knrpc.core.annotation.KNProvider;
import cn.lokn.knrpc.demo.api.User;
import cn.lokn.knrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public long getId(User user) {
        return user.getId();
    }

    @Override
    public long getId(float id) {
        return 1L;
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
    public User[] findUsers(User[] users) {
        return users;
    }

    @Override
    public List<User> getList(List<User> userList) {
        return userList;
    }

    @Override
    public Map<String, User> getMap(Map<String, User> userMap) {
        return userMap;
    }

    @Override
    public Boolean getFlag(boolean flag) {
        return flag;
    }

    @Override
    public User findById(long id) {
        return new User(Long.valueOf(id).intValue(), "kn");
    }

    @Override
    public User ex(boolean flag) {
        if (flag) throw new RuntimeException("just throw an exception");
        return new User(45, "kn+" + false);
    }

    @Override
    public long[] getLongIds() {
        return new long[]{1L, 3L, 5L};
    }

}
