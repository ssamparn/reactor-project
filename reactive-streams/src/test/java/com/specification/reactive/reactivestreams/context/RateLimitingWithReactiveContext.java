package com.specification.reactive.reactivestreams.context;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Rate limiting implementation using Reactive Context
 * Imagine we are calling a book service. It's a paid service like Kindle Unlimited. So we would want to rate limit it.
 * Requirement:
 * - For STANDARD users: Allow 2 calls every 5 seconds
 * - For PRIME users: Allow 3 calls every 5 seconds
 */
@Slf4j
public class RateLimitingWithReactiveContext {

    public enum UserRole {
        STANDARD, PRIME
    }

    static class RateLimiter {
        private final Map<String, Deque<Instant>> userRequestLog = new ConcurrentHashMap<>();

        public Mono<Boolean> isAllowed(String userId, UserRole userRole) {
            int limit = userRole == UserRole.PRIME ? 3 : 2;
            Duration window = Duration.ofSeconds(5);
            return Mono.fromSupplier(() -> {
                Deque<Instant> timestamps = userRequestLog.computeIfAbsent(userId, k -> new ConcurrentLinkedDeque<>());
                Instant now = Instant.now();
                synchronized (timestamps) {
                    while (!timestamps.isEmpty() && Duration.between(timestamps.peek(), now).compareTo(window) > 0) {
                        timestamps.poll();
                    }
                    if (timestamps.size() < limit) {
                        timestamps.add(now);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }


    @Test
    public void rate_limit_book_service_test() {
        BookService bookService = new BookService();

        String userId1 = "sam";
        UserRole role1 = UserRole.STANDARD;

        String userId2 = "mike";
        UserRole role2 = UserRole.PRIME;

        for (int i = 0; i < 10; i++) {
            bookService.getBook(userId2)
                    .contextWrite(Context.of("role", role2))
                    .subscribe(RsUtil.subscriber());
            RsUtil.sleepSeconds(1);
        }
    }

    /**
     * Sample book service
     */
    static class BookService {

        private final RateLimiter rateLimiter = new RateLimiter();

        public Mono<String> getBook(String userId) {
            return Mono.deferContextual(ctx -> {
                UserRole role = ctx.get("role");
                return rateLimiter.isAllowed(userId, role)
                        .flatMap(allowed -> {
                            if (allowed) {
                                return Mono.just(RsUtil.faker().book().title().concat(" book delivered to " + userId + " (" + role + ")"));
                            } else {
                                return Mono.error(new RuntimeException("Rate limit exceeded for " + userId + " (" + role + ")"));
                            }
                        });
            });
        }
    }
}
