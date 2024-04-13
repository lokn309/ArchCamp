package cn.lokn.knrpc.core.config;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @description:
 * @author: lokn
 * @date: 2024/04/13 16:38
 */
@Data
@Slf4j
public class ApolloChangedListener implements ApplicationContextAware {

    ApplicationContext applicationContext;

    // 此处监听的是 apollo 配置中的namespace名称
    @ApolloConfigChangeListener({"lokn", "application"})
    private void changeHandler(ConfigChangeEvent changeEvent) {
        for (String key : changeEvent.changedKeys()) {
            final ConfigChange change = changeEvent.getChange(key);
            log.info(" Found change - {}", change.toString());
        }
        applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
    }

}
