package com.specification.reactive.reactivestreams.model;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderStream {
    private String item;
    private double price;
    private String category;
    private int quantity;

    public OrderStream() {
        this.item = RsUtil.faker().commerce().productName();
        this.category = RsUtil.faker().commerce().department();
        this.price = Double.parseDouble(RsUtil.faker().commerce().price());
        this.quantity = RsUtil.faker().random().nextInt(1,10);
    }
}
