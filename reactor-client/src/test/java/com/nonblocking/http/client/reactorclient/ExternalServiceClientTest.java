package com.nonblocking.http.client.reactorclient;

import com.nonblocking.http.client.reactorclient.impl.ExternalServiceClient;
import com.nonblocking.http.client.reactorclient.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

/* *
 * To demo non-blocking I/O
 * Ensure that the external service is up and running!!
 * */
@Slf4j
public class ExternalServiceClientTest {

    private ExternalServiceClient client = new ExternalServiceClient();

    @Test
    public void externalServiceClientTest() {
        log.info("Starting");

        Mono<String> productMono = client.getProductName(1);

        productMono.subscribe(
                product -> log.info("product received: {}", product),
                err -> log.error("error occurred: {}", err.getMessage()),
                () -> log.info("Completed"));

        Util.sleepSeconds(2);
    }

    @Test
    public void externalServiceClientMultipleCallsNonBlockingIOTest() {
        log.info("Starting");

        for (int i = 1; i <= 5; i++) {
            client.getProductName(i).subscribe(
                    product -> log.info("product received: {}", product),
                    err -> log.error("error occurred: {}", err.getMessage()),
                    () -> log.info("Completed"));
        }

        // If you observe clearly, all 5 requests are executed by a single thread and all 5 requests happened more or less at the same time.
        // That is why order is not maintained. This is exactly what non-blocking IO is.

        Util.sleepSeconds(2);
    }
}