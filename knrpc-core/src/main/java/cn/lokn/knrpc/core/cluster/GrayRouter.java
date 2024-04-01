package cn.lokn.knrpc.core.cluster;

import cn.lokn.knrpc.core.api.Router;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

/**
 * @description: 灰度路由
 * @author: lokn
 * @date: 2024/04/01 00:03
 */
@Data
public class GrayRouter implements Router<InstanceMeta> {

    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {
        return null;
    }

}
