package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.OrderStream;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Objects;

/* *
 * OrderStream Service:
 *      - Provides a Stream of Orders.
 *      - OrderStream Stream will contain item, category, price (total price, it is not price per quantity) & quantity
 *      - Needs a minimum 2 subscribers to emit item
 * */
public class OrderStreamService {

    private Flux<OrderStream> orderFlux;

    public Flux<OrderStream> getOrderStream() {
        if (Objects.isNull(orderFlux)) {
            orderFlux = createOrderStream();
        }
        return orderFlux;
    }

    private Flux<OrderStream> createOrderStream() {
        return Flux.interval(Duration.ofMillis(100))
                .map(i -> new OrderStream())
                .publish()
                .refCount(2);
    }
}
