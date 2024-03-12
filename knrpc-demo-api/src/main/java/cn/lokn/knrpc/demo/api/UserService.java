package cn.lokn.knrpc.demo.api;

/**
 *
 */
public interface UserService {

    User findById(int id);

    User findById(int id, String name);

    long getId(long id);

    int getId(User user);

    String getName();

}
