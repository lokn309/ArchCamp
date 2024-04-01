package cn.lokn.knrpc.core.meta;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @description: 描述 provider 的映射关系
 * @author: lokn
 * @date: 2024/03/13 20:18
 */
@Data
@Builder
public class ProviderMeta {

    Method method;
    String methodSign;  // 避免反复反射构建方法
    Object serviceImpl;

}
