package cn.lokn.knrpc.core.util;

import cn.lokn.knrpc.core.annotation.KNConsumer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
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

    public static boolean checkLocalMethod(Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    /**
     * 获取签名类型
     *
     * @param method
     * @return 返回格式：methodName@1_string
     */
    public static String methodSign(Method method) {
        StringBuilder methodSign = new StringBuilder();
        methodSign.append(method.getName())
                .append("@")
                .append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes()).forEach(clazz -> {
            methodSign.append("_").append(clazz.getName());
        });
        return methodSign.toString();
    }

    /**
     * 获取所有带有注解 {@link KNConsumer} 的属性
     */
    public static List<Field> findAnnotatedField(Class<?> aClass,
                                                 Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        // 注意此处需要获取到父类，所以此处采用 while 循环获取
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }

}
