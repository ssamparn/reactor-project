package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.PurchaseOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseOrderService {

    private static Map<Integer, List<PurchaseOrder>> mockDb = new HashMap<>(); // key of the map is userId.

    static {
        List<PurchaseOrder> purchaseOrders1 = List.of(
                new PurchaseOrder(1),
                new PurchaseOrder(1),
                new PurchaseOrder(1)
        );

        List<PurchaseOrder> purchaseOrders2 = List.of(
                new PurchaseOrder(2),
                new PurchaseOrder(2)
        );

        List<PurchaseOrder> purchaseOrders3 = List.of(
                new PurchaseOrder(3),
                new PurchaseOrder(3),
                new PurchaseOrder(3),
                new PurchaseOrder(3)
        );

        mockDb.put(1, purchaseOrders1);
        mockDb.put(2, purchaseOrders2);
        mockDb.put(3, purchaseOrders3);

        // e.g: mockDb = Map<userId, [order1, order2, order3]>
    }

    public static Flux<PurchaseOrder> getOrders(int userId) {
        return Flux.create((FluxSink<PurchaseOrder> purchaseOrderSynchronousSink) -> {
            mockDb.get(userId).forEach(purchaseOrderSynchronousSink::next);
            purchaseOrderSynchronousSink.complete();
        }).delayElements(Duration.ofMillis(50));
    }
}
