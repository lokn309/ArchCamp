package cn.lokn.knrpc.core.registry;

public interface ChangedListener {
    void fire(Event event);
}
