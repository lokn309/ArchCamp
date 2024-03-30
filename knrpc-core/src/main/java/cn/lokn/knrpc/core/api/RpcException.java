package cn.lokn.knrpc.core.api;

import lombok.Data;

/**
 * @description: RPC 统一异常类
 * @author: lokn
 * @date: 2024/03/27 20:19
 */
@Data
public class RpcException extends RuntimeException {

    // TODO 可以通过枚举来实现
    private String errcode;

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(Throwable cause, String errcode) {
        super(cause);
        this.errcode = errcode;
    }

    // X => 技术类异常：可以通过重试操作，可能会执行成功
    // Y => 业务类异常：通过重试，是不会获取成功，所以业务类直接抛出异常
    // Z => unknown：搞不清楚，等后期清楚后，再归类到X或Y
    public static final String SocketTimeoutEx = "X001" + "-" + "http_invoke_timeout";
    public static final String NoSuchMethodEx = "X001" + "-" + "http_invoke_timeout";
    public static final String UnknownEx = "Z001" + "-" + "unknown";


    // TODO 作业
    // 用枚举的方式实现统一的异常；

}
