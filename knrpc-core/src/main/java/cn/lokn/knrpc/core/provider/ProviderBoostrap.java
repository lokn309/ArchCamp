package cn.lokn.knrpc.core.provider;

import cn.lokn.knrpc.core.utils.MethodUtils;
import cn.lokn.knrpc.core.annotation.KNProvider;
import cn.lokn.knrpc.core.api.RpcRequest;
import cn.lokn.knrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: provider 启动类
 * @author: lokn
 * @date: 2024/03/07 23:27
 */
@Data
public class ProviderBoostrap implements ApplicationContextAware {

    /**
     * 当 {@link ProviderBoostrap } 被丢到 spring 容器后，applicationContext 会被自动加载到容器中
     * 注意：属性名称需要用 applicationContext
     */
    ApplicationContext applicationContext; // 获取所有的bean

    // 获取所有的provider
    private Map<String, Object> skeleton = new HashMap<>();

    public RpcResponse invoke(RpcRequest request) {
        if (MethodUtils.checkLocalMethod(request.getMethod())) {
            return null;
        }
        final Object bean = skeleton.get(request.getService());
        RpcResponse rpcResponse = new RpcResponse();
        try {
            Method method = findMethod(bean.getClass(), request.getMethod(), request.getMethodSign());
            final Object result = method.invoke(bean, request.getArgs());
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
            return rpcResponse;
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        } catch (Exception e) {
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }
        return rpcResponse;
    }

    /**
     * @param aClass
     * @param methodName
     * @return
     */
    private Method findMethod(Class<?> aClass, String methodName) {
        // TODO 此处需要解决重载的问题
        for (Method method : aClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    private Method findMethod(Class<?> aClass, String methodName, String methodSign) {
        if (methodSign == null) {
            methodSign = "";
        }
        // TODO 此处需要解决重载的问题
        for (Method method : aClass.getMethods()) {
            final String sign = MethodUtils.methodSign(method);
            if (method.getName().equals(methodName) && (sign.equals(methodSign))) {
                return method;
            }

        }
        return null;
    }

    /**
     * 构建获取所有的provider
     */
    @PostConstruct
    public void buildProviders() {
        // 获取所有加了自定义 @KNProvider 注解的bean
        final Map<String, Object> providers = applicationContext.getBeansWithAnnotation(KNProvider.class);
        providers.values().forEach(
                this::genInterface
        );
    }

    private void genInterface(Object x) {
        final Class<?> itfer = x.getClass().getInterfaces()[0];
        skeleton.put(itfer.getCanonicalName(), x);
    }

}
