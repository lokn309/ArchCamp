package cn.lokn.knrpc.core.registry;

import cn.lokn.knrpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/20 23:27
 */
@Data
@AllArgsConstructor
public class Event {

    List<InstanceMeta> data;

}
