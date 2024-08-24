package com.nonblocking.http.client.assignment.zip;

import com.nonblocking.http.client.reactorclient.impl.ExternalServiceClient;
import com.nonblocking.http.client.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class ZipAssignmentTest {

    private ExternalServiceClient client = new ExternalServiceClient();

    @Test
    public void zipAssignmentTest() {
        for (int i = 1; i <= 10; i++) { // for each loop there is a subscription (10 subscriptions). See FlatmapAssignmentTest.class to achieve the same result with a single subscription
            createProductInformation(i)
            .subscribe(product -> log.info("product received: {}", product),
                    err -> log.error("error occurred: {}", err.getMessage()),
                    () -> log.info("Completed"));
        }
        Util.sleepSeconds(2);
    }

    record ProductInformation(String productName, String price, String review) {

    }

    private Mono<ProductInformation> createProductInformation(int productId) {
        return Mono.zip(
                client.getDemo05ProductName(productId),
                client.getDemo05PriceName(productId),
                client.getDemo05ReviewName(productId)
        )
                .map(productTuple -> new ProductInformation(productTuple.getT1(), productTuple.getT2(), productTuple.getT3()));
    }
}
