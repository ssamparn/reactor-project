package com.nonblocking.http.client.reactorclient.impl;

import com.nonblocking.http.client.reactorclient.AbstractHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class ExternalServiceClient extends AbstractHttpClient {

    // http://localhost:7070/demo01/product/{productId}
    public Mono<String> getProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo01/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    // http://localhost:7070/demo02/name/stream
    public Flux<String> getNameStream() {
        return this.httpClient.get()
                .uri("/demo02/name/stream")
                .responseContent()
                .asString();
    }

    // http://localhost:7070/demo02/stock/stream
    public Flux<Integer> getStockPrices() {
        return this.httpClient.get()
                .uri("/demo02/stock/stream")
                .responseContent()
                .asString()
                .map(Integer::valueOf);
    }

    // http://localhost:7070/demo03/product/{productId}
    public Mono<String> getDemo03ProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo03/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    // http://localhost:7070/demo03/empty-fallback/product/{productId}
    public Mono<String> getDemo03EmptyFallbackProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo03/empty-fallback/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    // http://localhost:7070/demo03/timeout-fallback/product/{productId}
    public Mono<String> getDemo03TimeoutFallbackProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo03/timeout-fallback/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    // http://localhost:7070/demo04/orders/stream
    public Flux<String> getDemo04OrdersStream() {
        return this.httpClient.get()
                .uri("/demo04/orders/stream")
                .responseContent()
                .asString();
    }

    // simple product publisher implementation
    // http://localhost:7070/demo01/product/{productId}
    public Mono<String> getProductNameWithScheduler(int productId) {
        return this.httpClient.get()
                .uri("/demo01/product/" + productId)
                .responseContent()
                .asString()
                .doOnNext(product -> log.info("next: {}", product)) // will be executed by the nio thread.
                .next()
                .publishOn(Schedulers.boundedElastic()); // the idea here is to free the nio threads.
        // When the events come from top to bottom, it encounters publishOn. Then nio threads will off load the task to bounded elastic thread pool.
    }
}