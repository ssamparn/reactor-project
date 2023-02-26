package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.PurchaseOrder;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseOrderService {

    private static Map<Integer, List<PurchaseOrder>> mockDb = new HashMap<>();

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

        mockDb.put(1, purchaseOrders1);
        mockDb.put(2, purchaseOrders2);
    }

    public static Flux<PurchaseOrder> getOrders(int userId) {
        return Flux.create(purchaseOrderSynchronousSink -> {
            mockDb.get(userId).forEach(purchaseOrderSynchronousSink::next);
            purchaseOrderSynchronousSink.complete();
        });
    }
}
