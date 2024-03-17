package cn.lokn.knrpc.demo.api;

import java.util.List;

/**
 *
 */
public interface UserService {

    User findById(int id);

    User findById(int id, String name);

    long getId(long id);

    int getId(User user);

    String getName();

    String getName(int id);

    int[] getIds();

    int[] getIds(int[] ids);

    long[] getLongIds();

    List<User> getLists();



}
