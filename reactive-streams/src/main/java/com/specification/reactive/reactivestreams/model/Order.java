package com.specification.reactive.reactivestreams.model;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Order {
    private int userId;
    private String productName;
    private String category;
    private Integer price;

    public Order(int userId) {
        this.userId = userId;
        this.productName = RsUtil.faker().commerce().productName();
        this.price = RsUtil.faker().random().nextInt(10, 100);
    }

    /* *
     * Used in groupBy() assignment
     * */

    public Order() {
        this.productName = RsUtil.faker().commerce().productName();
        this.category = RsUtil.faker().commerce().department();
        this.price = RsUtil.faker().random().nextInt(10, 100);
    }

    public Order(String productName, String category, Integer price) {
        this.productName = productName;
        this.category = category;
        this.price = price;
    }
}
