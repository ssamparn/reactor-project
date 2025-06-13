package com.nonblocking.http.client.assignment.context;

import com.nonblocking.http.client.reactorclient.impl.ExternalServiceClient;
import com.nonblocking.http.client.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.util.context.Context;

/**
 * Rate limiting implementation using Reactive Context
 * Imagine we are calling a book service. It's a paid service like Kindle Unlimited. So we would want to rate limit it.
 * Requirement:
 * - For STANDARD users: Allow 2 calls every 5 seconds
 * - For PRIME users: Allow 3 calls every 5 seconds
 */

@Slf4j
public class RateLimitingWithReactiveContextTest {

    private ExternalServiceClient client = new ExternalServiceClient();

    @Test
    public void rate_limit_book_service_test() {
        for (int i = 0; i < 10; i++) {
            client.getBook()
                    .contextWrite(Context.of("user", "mike"))
                    .subscribe(book -> log.info("book received: {}", book),
                            err -> log.error("error occurred: {}", err.getMessage()),
                            () -> log.info("Completed"));
            Util.sleepSeconds(1);
        }
    }

}
