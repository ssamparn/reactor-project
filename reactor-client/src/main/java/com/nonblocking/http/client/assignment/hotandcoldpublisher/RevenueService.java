package com.nonblocking.http.client.assignment.hotandcoldpublisher;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/* *
 * Revenue Service: Consumes Order Streams emitted from the Order Service and provides "revenue" per order category.
 *                  And more over Revenue Service will emit its data every 2 seconds.
 * */
public class RevenueService implements OrderProcessor {

    private Map<String, Double> revenue = new HashMap<>();

    @Override
    public void consume(Order order) {
        double currentRevenue = revenue.getOrDefault(order.category(), 0.0);
        double updatedRevenue = currentRevenue + order.price();
        revenue.put(order.category(), updatedRevenue);
    }

    @Override
    public Flux<String> stream() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(i -> revenue.toString());
    }
}
