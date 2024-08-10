package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.Order;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Objects;

/**
 * Order Service: Provides a Stream of Orders.
 *                Order Stream will contain item, category, price (total price, it is not price per quantity) & quantity
 *                Needs a minimum 2 subscribers to emit item
 * */
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
