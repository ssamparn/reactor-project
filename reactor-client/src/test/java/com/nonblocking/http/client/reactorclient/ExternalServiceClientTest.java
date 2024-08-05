package com.nonblocking.http.client.reactorclient;

import com.nonblocking.http.client.reactorclient.impl.ExternalServiceClient;
import com.nonblocking.http.client.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;


@Slf4j
public class ExternalServiceClientTest {

    private ExternalServiceClient client = new ExternalServiceClient();

    /* *
     * To demo non-blocking I/O
     * Ensure that the external service is up and running!!
     * */

    @Test
    public void getProductExternalServiceClientTest() {
        log.info("Starting");

        Mono<String> productMono = client.getProductName(1);

        productMono.subscribe(
                product -> log.info("product received: {}", product),
                err -> log.error("error occurred: {}", err.getMessage()),
                () -> log.info("Completed"));

        Util.sleepSeconds(2);
    }

    @Test
    public void getProductsExternalServiceClientMultipleCallsNonBlockingIOTest() {
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

    /* *
     * To demo non-blocking I/O with streaming messages
     * Ensure that the external service is up and running!!
     * */

    @Test
    public void getNameStreamExternalServiceClientNonBlockingIOTest() {
        log.info("Starting");

        client.getNameStream()
                .subscribe(
                        Util.onNext(),
                        Util.onError(),
                        Util.onComplete()
                );

        Util.sleepSeconds(6);
    }
}