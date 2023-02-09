package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.util.ReactiveSpecificationUtil;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class StockPricePublisher {

    public static Flux<Integer> getPrice() {
        AtomicInteger atomicInteger = new AtomicInteger(100);

        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> atomicInteger.getAndAccumulate(ReactiveSpecificationUtil.faker().random().nextInt(-5, 5), Integer::sum));
    }
}
