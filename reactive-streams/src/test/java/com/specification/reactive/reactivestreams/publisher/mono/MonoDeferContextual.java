package com.specification.reactive.reactivestreams.publisher.mono;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class MonoDeferContextual {

    /* *
     * https://spring.io/blog/2023/03/28/context-propagation-with-project-reactor-1-the-basics
     * */

    static final ThreadLocal<Long> CORRELATION_ID = new ThreadLocal<>();

    static long correlationId() {
        return Math.abs(ThreadLocalRandom.current().nextLong());
    }

    static void log(String message) {
        String threadName = Thread.currentThread().getName();
        String threadNameTail = threadName.substring(
                Math.max(0, threadName.length() - 10));
        System.out.printf("[%10s][%20s] %s%n",
                threadNameTail, CORRELATION_ID.get(), message);
    }

    static void log(String message, long correlationId) {
        String threadName = Thread.currentThread().getName();
        String threadNameTail = threadName.substring(
                Math.max(0, threadName.length() - 10));
        System.out.printf("[%10s][%20s] %s%n",
                threadNameTail, correlationId, message);
    }

    static Mono<Void> addProduct(String productName) {
        return Mono.deferContextual(ctx -> {
            log("Adding product " + productName, ctx.get("CORRELATION_ID"));
            return Mono.empty();
        });
    }

    static Mono<Boolean> notifyShop(String productName) {
        return Mono.deferContextual(ctx -> {
            log("Notifying shop " + productName, ctx.get("CORRELATION_ID"));
            return Mono.just(true);
        });
    }

    Mono<Void> handleRequest() {
        long correlationId = correlationId();
        log("Assembling Chain: ", correlationId);

        return Mono.just("test-product")
                .delayElement(Duration.ofMillis(1))
                .flatMap(product -> Flux.concat(addProduct(product), notifyShop(product))
                        .then())
                .contextWrite(Context.of("CORRELATION_ID", correlationId));
    }

    @Test
    public void mono_deferContextual_test() {
        handleRequest()
                .subscribe();
    }

}
