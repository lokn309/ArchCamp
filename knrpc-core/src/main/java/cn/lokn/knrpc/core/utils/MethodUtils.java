package cn.lokn.knrpc.core.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/12 00:19
 */
public class MethodUtils {

    private static Set<String> methodSet = new HashSet<>();

    static {
        final Class<Object> objectClass = Object.class;
        final Method[] methods = objectClass.getMethods();
        methodSet = Arrays.stream(methods).map(Method::getName).collect(Collectors.toSet());
    }

    public static boolean checkLocalMethod(String method) {
        return methodSet.contains(method);
    }

    /**
     * 获取签名类型
     *
     * @param method
     * @return 返回格式： xx_xxx,xxx,xxx   eg: 2_String,int
     */
    public static String methodSign(Method method) {
        if (method != null) {
            StringBuilder methodSign = new StringBuilder();
            final int parameterCount = method.getParameterCount();
            if (parameterCount < 1) {
                return "";
            }
            methodSign.append(parameterCount).append("_");
            final Class<?>[] parameterTypes = method.getParameterTypes();
            Arrays.stream(parameterTypes).forEach(clazz -> {
                methodSign.append(clazz.getName()).append(",");
            });
            methodSign.delete(methodSign.length() - 1, methodSign.length());
            return methodSign.toString();
        }
        return "";
    }

}
