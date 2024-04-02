package cn.lokn.knrpc.core.cluster;

import cn.lokn.knrpc.core.api.Router;
import cn.lokn.knrpc.core.meta.InstanceMeta;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @description: 灰度路由
 * @author: lokn
 * @date: 2024/04/01 00:03
 */
@Slf4j
@Data
public class GrayRouter implements Router<InstanceMeta> {

    private int grayRatio;

    private final Random random = new Random();

    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {

        if (providers == null || providers.size() <= 1) {
            return providers;
        }

        List<InstanceMeta> normalNodes = new ArrayList<>();
        List<InstanceMeta> grayNodes = new ArrayList<>();

        providers.forEach(p -> {
            if ("true".equals(p.getParameters().get("gray"))) {
                grayNodes.add(p);
            } else {
                normalNodes.add(p);
            }
        });

        log.debug(" grayRouter grayNodes/normalNodes, grayRatio ===>{}/{},{}",
                grayNodes.size(), normalNodes.size(), grayRatio);

        if (normalNodes.isEmpty() || grayNodes.isEmpty()) return providers;
        if (grayRatio <= 0)  return normalNodes;
        if (grayRatio >= 100) return grayNodes;

        // 此处需要处理 1 < grayRatio < 100 的时候
        // 需要考虑 gray 和 normal 的返回比例情况
        // 技巧：采用随机数来实现
        if (random.nextInt(100) < grayRatio) {
            log.debug(" grayRouter grayNodes ===> {}", grayNodes);
            return grayNodes;
        } else {
            log.debug(" grayRouter normalNodes ===> {}", normalNodes);
            return normalNodes;
        }
    }

}
