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

    /* *
     * Product Service:
     * GET http://localhost:7070/demo01/product/{productId}
     * Provides the product name for the given product id (up to product id 100)
     * */
    public Mono<String> getProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo01/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    /* *
     * Streaming Service:
     * GET http://localhost:7070/demo02/name/stream
     * Generates random first names every 500 ms!
     * */
    public Flux<String> getNameStream() {
        return this.httpClient.get()
                .uri("/demo02/name/stream")
                .responseContent()
                .asString();
    }

    /* *
     * Assignment: Stock Service:
     * GET http://localhost:7070/demo02/stock/stream
     * Sends stock price to the observer periodically! The stock price can be between 80 - 120. This service will emit price changes every 500ms for ~20 seconds.
     * */
    public Flux<Integer> getStockPrices() {
        return this.httpClient.get()
                .uri("/demo02/stock/stream")
                .responseContent()
                .asString()
                .map(Integer::valueOf);
    }

    /* *
     * Product Service:
     * GET http://localhost:7070/demo03/product/{productId}
     * Provides the product name for the given product id (1,2,3,4)
     * */
    public Mono<String> getDemo03ProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo03/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    /* *
     * Fallback Product Service for Empty Events:
     * GET http://localhost:7070/demo03/empty-fallback/product/{productId}
     * Provides the product name for the given product id (1,2,3,4)
     * */
    public Mono<String> getDemo03EmptyFallbackProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo03/empty-fallback/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    /* *
     * Fallback Product Service for timing out:
     * GET http://localhost:7070/demo03/timeout-fallback/product/{productId}
     * Provides the product name for the given product id (1,2,3,4)
     * */
    public Mono<String> getDemo03TimeoutFallbackProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo03/timeout-fallback/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    /* *
     * Orders Stream
     * GET http://localhost:7070/demo04/orders/stream
     * Provides stream of orders
     * */
    public Flux<String> getDemo04OrdersStream() {
        return this.httpClient.get()
                .uri("/demo04/orders/stream")
                .responseContent()
                .asString();
    }

    /**
     * Simple product publisher implementation
     * GET http://localhost:7070/demo01/product/{productId}
     * Provides the product name for the given product id (up to product id 100)
     * */

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

    /* *
     * Price Service:
     * GET http://localhost:7070/demo05/price/{productId}
     * Gives the price for product ids 1 - 10. Takes 1 second to respond.
     * */
    public Mono<String> getDemo05PriceName(int productId) {
        return this.httpClient.get()
                .uri("/demo05/price/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    /* *
     * Product Name Service:
     * GET http://localhost:7070/demo05/product/{productId}
     * Gives the product name for product ids 1 - 10. Takes 1 second to respond.
     * */
    public Mono<String> getDemo05ProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo05/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    /* *
     * Review Service:
     * GET http://localhost:7070/demo05/review/{productId}
     * Gives the review for product ids 1 - 10. Takes 1 second to respond.
     * */
    public Mono<String> getDemo05ReviewName(int productId) {
        return this.httpClient.get()
                .uri("/demo05/review/" + productId)
                .responseContent()
                .asString()
                .next();
    }
}