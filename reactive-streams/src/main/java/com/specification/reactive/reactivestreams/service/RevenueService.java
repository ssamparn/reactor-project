package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.Order;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/* *
 * Revenue Service: Consumes Order Streams emitted from the Order Service and provides "revenue" per order category.
 *                  And more over Revenue Service will emit its data every 2 seconds.
 * */
public class RevenueService {

    private Map<String, Double> revenue = new HashMap<>();

    public RevenueService() {
        revenue.put("Kids", 0.0);
        revenue.put("Automotive", 0.0);
    }

    public Consumer<Order> subscribeOrderStream() {
        return order -> revenue.computeIfPresent(order.getCategory(), (k, v) -> v + order.getPrice());
    }

    public Flux<String> revenueStream() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(i -> revenue.toString());
    }
}
