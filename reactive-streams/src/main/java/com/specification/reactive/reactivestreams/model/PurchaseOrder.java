package com.specification.reactive.reactivestreams.model;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PurchaseOrder {
    private int userId;
    private String item;
    private String price;

    public PurchaseOrder(int userId) {
        this.userId = userId;
        this.item = RsUtil.faker().commerce().productName();
        this.price = RsUtil.faker().commerce().price();
    }
}
