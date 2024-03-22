package cn.lokn.knrpc.core.util;

import cn.lokn.knrpc.core.api.RpcResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/15 14:27
 */
public class TypeUtils {

    /**
     *
     * @param origin    返回结果
     * @param type      返回值类型
     * @return
     */
    public static Object cast(Object origin, Class<?> type) {
        if (origin == null) return null;
        Class<?> aClass = origin.getClass();
        if (type.isAssignableFrom(aClass)) {
            return origin;
        }
        if (type.isArray()) {
            if (origin instanceof List list) {
                origin = list.toArray();
            }
            int length = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            Object array = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                // TODO 待添加原生类型 和 jdk自身类型处理
                Array.set(array, i, Array.get(origin, i));
            }
            return array;
        }
        if (origin instanceof HashMap map) {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return Integer.valueOf(origin.toString());
        }
        if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return Long.valueOf(origin.toString());
        }
        if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return Float.valueOf(origin.toString());
        }
        if (type.equals(Double.class) || type.equals(Double.TYPE)) {
            return Double.valueOf(origin.toString());
        }
        if (type.equals(Short.class) || type.equals(Short.TYPE)) {
            return Short.valueOf(origin.toString());
        }
        if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
            return Byte.valueOf(origin.toString());
        }
        if (type.equals(Character.class) || type.equals(Character.TYPE)) {
            return origin.toString().charAt(0);
        }
        return origin;
    }

    public static Object castMethodResult(Method method, Object data) {
        if (data instanceof JSONObject jsonResult) {
            return jsonResult.toJavaObject(method.getReturnType());
        }
        if (data instanceof JSONArray jsonArray) {
            Object[] array = jsonArray.toArray();
            Class<?> componentType = method.getReturnType().getComponentType();
            final Object resultArray = Array.newInstance(componentType, array.length);
            for (int i = 0; i < array.length; i++) {
                Array.set(resultArray, i, array[i]);
            }
            return resultArray;
        }
        // TODO 待添加 List 和 Map 的处理逻辑

        return TypeUtils.cast(data, method.getReturnType());
    }

}
