package cn.lokn.knrpc.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/15 14:27
 */
@Slf4j
public class TypeUtils {

    /**
     * @param origin 返回结果
     * @param type   返回值类型
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
            Object resultArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                // 处理原生类型 和 jdk自身类型处理
                if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                    Array.set(resultArray, i, Array.get(origin, i));
                } else {
                    final Object castObject = cast(Array.get(origin, i), componentType);
                    Array.set(resultArray, i, castObject);
                }
            }
            return resultArray;
        }
        if (origin instanceof HashMap map) {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }

        if (origin instanceof JSONObject jsonObject) {
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

    /**
     * 方法返回结果值转化处理
     *
     * @param method
     * @param data
     * @return
     */
    public static Object castMethodResult(Method method, Object data) {
        final Class<?> type = method.getReturnType();
        log.info("method.getReturnType() = " + type);
        if (data instanceof JSONObject jsonResult) {
            // Map 类型结果处理
            if (Map.class.isAssignableFrom(type)) {
                Map resultMap = new HashMap();
                final Type genericReturnType = method.getGenericReturnType();
                log.info("genericReturnType = " + genericReturnType);
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    final Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    final Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
                    log.info("keyType   : " + keyType);
                    log.info("valueType : " + valueType);
                    jsonResult.entrySet().stream().forEach(
                            e -> {
                                final Object key = cast(e.getKey(), keyType);
                                final Object value = cast(e.getValue(), valueType);
                                resultMap.put(key, value);
                            }
                    );
                }
                return resultMap;
            }
            return jsonResult.toJavaObject(type);
        }
        if (data instanceof JSONArray jsonArray) {
            Object[] array = jsonArray.toArray();
            if (type.isArray()) {
                Class<?> componentType = method.getReturnType().getComponentType();
                final Object resultArray = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                        Array.set(resultArray, i, array[i]);
                    } else {
                        final Object cast = cast(array[i], componentType);
                        Array.set(resultArray, i, cast);
                    }
                }
                return resultArray;
            } else if (List.class.isAssignableFrom(type)) {
                List<Object> resultList = new ArrayList<>(array.length);
                final Type genericReturnType = method.getGenericReturnType();
                log.info(genericReturnType.toString());
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    final Type actualType = parameterizedType.getActualTypeArguments()[0];
                    log.info(actualType.toString());
                    for (Object o : array) {
                        resultList.add(cast(o, (Class<?>) actualType));
                    }
                } else {
                    resultList.addAll(Arrays.asList(array));
                }
                return resultList;
            } else {
                return null;
            }
        }
        return cast(data, method.getReturnType());
    }

}
