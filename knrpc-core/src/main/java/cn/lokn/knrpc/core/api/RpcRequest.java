package cn.lokn.knrpc.core.api;

import lombok.Data;

/**
 * @description: 用来描述请求
 * @author: lokn
 * @date: 2024/03/07 00:05
 */
@Data
public class RpcRequest {

    private String service; // 接口： cn.lokn.knrpc.demo.api.User
    private String method;  // 方法： findById
    private String methodSign; // 方法签名： 2_String,int
    private Object[] args;  // 参数： 100

}
