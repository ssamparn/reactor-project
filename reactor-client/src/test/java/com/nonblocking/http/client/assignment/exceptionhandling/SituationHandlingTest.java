package com.nonblocking.http.client.assignment.exceptionhandling;

import com.nonblocking.http.client.reactorclient.impl.ExternalServiceClient;
import com.nonblocking.http.client.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Assignment of Operators Section:
 *      - We have 4 product id's - 1, 2, 3, 4
 *      - Get the product name using product service. (primary service)
 *      - Timeout 2 seconds - Call Fallback for timeout service (fallback service returns product name) to get the product name.
 *      - Call fallback for empty service to get the product name in case the product name returned by the primary service is empty.
 * */
@Slf4j
public class SituationHandlingTest {

    private ExternalServiceClient client = new ExternalServiceClient();

    @Test
    public void productTimeoutAndFallbackTest() {
        log.info("Starting");

        for (int i = 1; i <= 4; i++) {
            Mono<String> productNameMono = client.getDemo03ProductName(i);
            Mono<String> productNameTimeoutMono = client.getDemo03TimeoutFallbackProductName(i);
            Mono<String> productNameEmptyMono = client.getDemo03EmptyFallbackProductName(i);

            productNameMono.timeout(Duration.ofSeconds(2), productNameTimeoutMono)
                    .switchIfEmpty(productNameEmptyMono)
                    .subscribe(
                        product -> log.info("product received: {}", product),
                        err -> log.error("error occurred: {}", err.getMessage()),
                        () -> log.info("Completed"));
        }
        Util.sleepSeconds(3);
    }
}