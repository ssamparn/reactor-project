package com.nonblocking.http.client.assignment.hotandcoldpublisher;

import reactor.core.publisher.Flux;

public interface OrderProcessor {
    void consume(Order order);
    Flux<String> stream();
}