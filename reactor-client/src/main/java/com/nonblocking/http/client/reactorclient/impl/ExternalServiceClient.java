package com.nonblocking.http.client.reactorclient.impl;

import com.nonblocking.http.client.reactorclient.AbstractHttpClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ExternalServiceClient extends AbstractHttpClient {

    public Mono<String> getProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo01/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    public Flux<String> getNameStream() {
        return this.httpClient.get()
                .uri("/demo02/name/stream")
                .responseContent()
                .asString();
    }

    public Flux<Integer> getStockPrices() {
        return this.httpClient.get()
                .uri("/demo02/stock/stream")
                .responseContent()
                .asString()
                .map(Integer::valueOf);
    }

    public Mono<String> getDemo03ProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo03/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    public Mono<String> getDemo03EmptyFallbackProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo03/empty-fallback/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }

    public Mono<String> getDemo03TimeoutFallbackProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo03/timeout-fallback/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }
}