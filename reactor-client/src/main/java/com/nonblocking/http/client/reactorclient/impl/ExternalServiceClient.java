package com.nonblocking.http.client.reactorclient.impl;

import com.nonblocking.http.client.reactorclient.AbstractHttpClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

}