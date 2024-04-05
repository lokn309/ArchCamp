package cn.lokn.knrpc.core.api;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 用来描述请求
 * @author: lokn
 * @date: 2024/03/07 00:05
 */
@Data
@ToString
public class RpcRequest {

    private String service; // 接口： cn.lokn.knrpc.demo.api.User
    private String methodSign; // 方法签名： 2_String,int
    private Object[] args;  // 参数： 100

    // 跨调用方传递需要的参数
    private Map<String, String> params = new HashMap<>();

}
