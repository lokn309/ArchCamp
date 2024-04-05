package cn.lokn.knrpc.core.meta;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 描述服务实例的元数据
 * @author: lokn
 * @date: 2024/03/23 00:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {

    private String scheme;
    private String host;
    private Integer port;
    private String context; // 路径上下文本

    private boolean status; // online or offline  TODO 0901 蓝绿发布
    private Map<String, String> parameters = new HashMap<>();

    public InstanceMeta(String scheme, String host, Integer port, String context) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http", host, port, "rpc");
    }

    public String getUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }

    public String toMetas() {
        return JSONObject.toJSONString(this.parameters);
    }

}
