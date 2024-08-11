package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.util.RsUtil;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The stock service will emit price changes every 500ms for 20 seconds.
 * The price might change between 80-120.
 *
 *      Task:
 *          - Create a subscriber with $1000 balance.
 *          - Whenever the price drops below 90, buy a stock.
 *          - When the price goes above 110,
 *              - Sell all the stocks.
 *              - Cancel the subscription.
 *              - Print the profit you made.
 * */

public class StockPricePublisher {

    public static Flux<Integer> getPrice() {
        AtomicInteger atomicInteger = new AtomicInteger(100);

        return Flux.interval(Duration.ofMillis(500))
                .map(i -> atomicInteger.getAndAccumulate(RsUtil.faker().random().nextInt(-5, 5), Integer::sum));
    }
}
