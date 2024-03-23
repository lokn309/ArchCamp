package cn.lokn.knrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @description:
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

    private boolean status; // online or offline
    private Map<String, String> parameters;

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public InstanceMeta(String scheme, String host, Integer port, String context) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http", host, port, "");
    }

    public String getUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }
}
