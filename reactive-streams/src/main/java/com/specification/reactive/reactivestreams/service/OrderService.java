package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.Order;
import com.specification.reactive.reactivestreams.util.RsUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* *
 * To be used in flatMap() demo.
 * To be used in concatMap() demo.
 * To be used in collectList() demo.
 * Imagine purchase order service as an application which has an endpoint to get all orders based on userId.
 * */
public class OrderService {

    private static Map<Integer, List<Order>> mockDb = new HashMap<>(); // key of the map is userId.

    static {
        List<Order> orders1 = List.of(
                new Order(1),
                new Order(1),
                new Order(1),
                new Order(1),
                new Order(1),
                new Order(1)
        );

        List<Order> orders2 = List.of(
                new Order(2),
                new Order(2),
                new Order(2)
        );

        List<Order> orders3 = List.of(
                new Order(3),
                new Order(3),
                new Order(3),
                new Order(3),
                new Order(3),
                new Order(3),
                new Order(3),
                new Order(3)
        );

        mockDb.put(1, orders1);
        mockDb.put(2, orders2);
        mockDb.put(3, orders3);

        // e.g: mockDb = Map<userId, [order1, order2, order3]>
    }

    public static Flux<Order> getUserOrders(int userId) {
        return Flux.create((FluxSink<Order> purchaseOrderSynchronousSink) -> {
                    mockDb.get(userId).forEach(purchaseOrderSynchronousSink::next);
                    purchaseOrderSynchronousSink.complete();
                }).delayElements(Duration.ofMillis(50))
                .transform(RsUtil.addDebugger("order-for-user" + userId));
    }
}
