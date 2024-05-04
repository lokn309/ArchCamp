package cn.lokn.knrpc.core.consumer;

import cn.lokn.knrpc.core.api.RpcRequest;
import cn.lokn.knrpc.core.api.RpcResponse;
import cn.lokn.knrpc.core.consumer.http.OkHttpInvoker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/23 00:09
 */
public interface HttpInvoker {

    Logger log = LoggerFactory.getLogger(HttpInvoker.class);

    HttpInvoker Default = new OkHttpInvoker(500);

    RpcResponse<?> post(RpcRequest rpcRequest, String url);

    String post(String requestString, String url);

    String get(String url);

    @SneakyThrows
    static <T> T httpGet(String url, Class<T> clazz) {
        log.debug(" ===>>> httpGet url: {}", url);
        final String result = Default.post(JSON.toJSONString(clazz), url);
        log.debug(" ===>>> httpGet result: {}", result);
        return JSON.parseObject(result, clazz);
    }

    @SneakyThrows
    static <T> T httpGet(String url, TypeReference<T> typeReference) {
        log.debug(" ===>>> httpGet url: {}", url);
        final String result = Default.get(url);
        log.debug(" ===>>> httpGet result: {}", result);
        return JSON.parseObject(result, typeReference);
    }

    @SneakyThrows
    static <T> T httpPost(String url, Class<T> clazz) {
        log.debug(" ===>>> httpPost url: {}", url);
        final String result = Default.post(JSON.toJSONString(clazz), url);
        log.debug(" ===>>> httpPost result: {}", result);
        return JSON.parseObject(result, clazz);
    }

    @SneakyThrows
    static <T> T httpPost(String url, String params, Class<T> clazz) {
        log.debug(" ===>>> httpPost url: {}", url);
        final String result = Default.post(params, url);
        log.debug(" ===>>> httpPost result: {}", result);
        return JSON.parseObject(result, clazz);
    }

}
