package cn.lokn.knrpc.core.meta;

import cn.lokn.knrpc.core.registry.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 描述服务元数据
 * @author: lokn
 * @date: 2024/03/23 14:57
 */
@Data
@Builder
public class ServiceMeta {

    private String app;
    private String namespace;
    private String env;
    private String name;

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);

    }
}
