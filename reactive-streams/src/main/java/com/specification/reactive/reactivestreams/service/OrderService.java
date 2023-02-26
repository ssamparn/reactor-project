package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.Order;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Objects;

public class OrderService {

    private Flux<Order> orderFlux;

    public Flux<Order> getOrderStream() {
        if (Objects.isNull(orderFlux)) {
            orderFlux = createOrderStream();
        }
        return orderFlux;
    }

    private Flux<Order> createOrderStream() {
        return Flux.interval(Duration.ofMillis(100))
                .map(i -> new Order())
                .publish()
                .refCount(2);
    }
}
