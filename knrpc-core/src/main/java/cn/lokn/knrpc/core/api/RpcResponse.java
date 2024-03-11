package cn.lokn.knrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/07 00:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {

    boolean status; // 状态：true
    T data;         // 数据：new User()
    Exception ex;

}
