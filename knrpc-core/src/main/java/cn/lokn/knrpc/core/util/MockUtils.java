package cn.lokn.knrpc.core.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/24 16:05
 */
public class MockUtils {
    public static Object mock(Class type) {
        if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return 1;
        }
        if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return 10000L;
        }
        if (Number.class.isAssignableFrom(type)) {
            return 1;
        }
        if (type.equals(String.class)) {
            return "this_is_a_mock_string";
        }

        return mockPojo(type);
    }

    @SneakyThrows
    private static Object mockPojo(Class type) {
        final Object result = type.getDeclaredConstructor().newInstance();
        final Field[] fields = type.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            final Class<?> fType = f.getType();
            Object fvalue = mock(fType);
            f.set(result, fvalue);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(mock(UserDTO.class));
    }

    static class UserDTO {
        private int a;
        private String b;

        @Override
        public String toString() {
            return a + "," + b;
        }
    }
}
