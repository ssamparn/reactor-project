package com.specification.reactive.reactivestreams.model;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Order {
    private String item;
    private double price;
    private String category;
    private int quantity;

    public Order() {
        this.item = RsUtil.faker().commerce().productName();
        this.price = Double.parseDouble(RsUtil.faker().commerce().price());
        this.category = RsUtil.faker().commerce().department();
        this.quantity = RsUtil.faker().random().nextInt(1,10);
    }
}
