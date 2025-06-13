package com.nonblocking.http.client.assignment.context;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * To be used in reactive context demo
 * */
@Slf4j
public class RateLimiter {

    private static final Map<String, Integer> categoryAttempts = Collections.synchronizedMap(new HashMap<>());

    static {
        refresh();
    }

    public static <T> Mono<T> limitCalls() {
        return Mono.deferContextual(ctx -> {
            boolean allowCall = ctx.<String>getOrEmpty("category")
                    .map(RateLimiter::canAllow)
                    .orElse(false);

            log.info("ctx: {}", ctx);
            return allowCall ? Mono.empty() : Mono.error(new RuntimeException("exceeded the given limit"));
        });
    }

    // synchronized used to make the method thread safe
    private static synchronized boolean canAllow(String category) {
        Integer attempt = categoryAttempts.getOrDefault(category, 0);
        if (attempt > 0) {
            categoryAttempts.put(category, attempt - 1);
            return true;
        }
        return false;
    }

    private static void refresh() {
        Flux.interval(Duration.ZERO, Duration.ofSeconds(5))
                .startWith(0L)
                .subscribe(i -> {
                    categoryAttempts.put("standard", 2);
                    categoryAttempts.put("prime", 3);
                });
    }
}
