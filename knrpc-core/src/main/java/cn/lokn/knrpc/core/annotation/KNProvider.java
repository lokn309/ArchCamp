package cn.lokn.knrpc.core.annotation;

import java.lang.annotation.*;

/**
 * 该注解表示被标记的类就是provider
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface KNProvider {

}
