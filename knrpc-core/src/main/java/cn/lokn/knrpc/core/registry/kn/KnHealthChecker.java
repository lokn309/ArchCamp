package cn.lokn.knrpc.core.registry.kn;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description: check health for registry center
 * @author: lokn
 * @date: 2024/05/08 07:53
 */
@Slf4j
public class KnHealthChecker {

    ScheduledExecutorService consumerExecutor = null;
    ScheduledExecutorService providerExecutor = null;

    public void start() {
        log.info(" ====>>> [KnRegistry] : start with health checker.");
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);
    }

    public void stop() {
        log.info(" ====>>> [KnRegistry] : stop with health check");
        gracefulShutDown(consumerExecutor);
        gracefulShutDown(providerExecutor);
    }

    public void consumerCheck(CallBack callBack) {
        consumerExecutor.scheduleWithFixedDelay(() -> {
            try {
                callBack.call();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    public void providerCheck(CallBack callBack) {
        providerExecutor.scheduleWithFixedDelay(() -> {
            try {
                callBack.call();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void gracefulShutDown(ScheduledExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public interface CallBack {
        void call() throws Exception;
    }

}
