package com.nonblocking.http.client.assignment.retry;

import com.nonblocking.http.client.reactorclient.exception.ServerError;
import com.nonblocking.http.client.reactorclient.impl.ExternalServiceClient;
import com.nonblocking.http.client.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
public class RetryAssignmentTest {

    private ExternalServiceClient externalServiceClient = new ExternalServiceClient();

    /* *
     * If we pass product id 1, then we will encounter a 400 Bad request, for which we will not retry.
     * Product id: 1 - 400 Bad Request
     * Product id: 2 - Random 500 Internal Server Error
     * */
    @Test
    public void noRetryOn400BadRequestAssignmentTest() {
        externalServiceClient.getProductNameForRetry(1)
                .retryWhen(retryOnServerError())
                .subscribe(country -> log.info("product received: {}", country),
                        err -> log.error("error occurred: {}", err.getMessage()),
                        () -> log.info("Completed"));

        Util.sleepSeconds(5);
    }

    @Test
    public void retryOn500InternalServerErrorAssignmentTest() {
        externalServiceClient.getProductNameForRetry(2)
                .retryWhen(retryOnServerError())
                .subscribe(country -> log.info("product received: {}", country),
                        err -> log.error("error occurred: {}", err.getMessage()),
                        () -> log.info("Completed"));

        Util.sleepSeconds(6);
    }

    private Retry retryOnServerError() {
        return Retry.fixedDelay(10, Duration.ofMillis(400))
                .filter(ex -> ServerError.class.equals(ex.getClass()))
                .doBeforeRetry(rs -> log.info("retrying failure: {}", rs.failure().getMessage()));
    }
}
