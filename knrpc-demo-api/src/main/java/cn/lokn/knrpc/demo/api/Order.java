package cn.lokn.knrpc.demo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: lokn
 * @date: 2024/03/11 23:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    Integer id;
    Float amount;

}
