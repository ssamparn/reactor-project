package com.specification.reactive.reactivestreams.service;

import reactor.core.publisher.Mono;

import java.util.Map;

/* *
 * To be used in flatMap() demo.
 * Imagine payment-service, as an application, has an endpoint.
 * Imagine there would be a client class which will call this endpoint (I/O request)
 * */
public class PaymentService {

    private static final Map<Integer, Integer> paymentsDb = Map.of(
            1, 100,
            2, 200,
            3, 300
    );

    public static Mono<Integer> getUserBalance(Integer userId) {
        return Mono.fromSupplier(() -> paymentsDb.getOrDefault(userId, 0));
    }

}
