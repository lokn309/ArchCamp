package cn.lokn.knrpc.core.meta;

import cn.lokn.knrpc.core.registry.Event;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 描述服务的元数据
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

    private Map<String, String> parameters = new HashMap<>();

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }

    public String toMetas() {
        return JSONObject.toJSONString(this.parameters);
    }

}
