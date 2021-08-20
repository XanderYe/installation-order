package cn.xanderye.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author XanderYe
 * @description:
 * @date 2021/8/19 20:13
 */
@Data
public class Part {
    private String name;

    private BigDecimal price;

    private String link;

    private Integer type;

    private Integer num;

    private BigDecimal totalPrice;
}
