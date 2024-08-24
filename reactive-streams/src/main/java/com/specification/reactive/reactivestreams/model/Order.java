package com.specification.reactive.reactivestreams.model;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Order {
    private int userId;
    private String productName;
    private String price;

    public Order(int userId) {
        this.userId = userId;
        this.productName = RsUtil.faker().commerce().productName();
        this.price = RsUtil.faker().commerce().price();
    }
}
