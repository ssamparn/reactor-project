package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
public class ZipAssignmentTest {

    record ProductInformation(String productName, String price, String review) {

    }

    @Test
    public void zipAssignmentTest() {
        for (int i = 1; i <= 10; i++) {
            Mono.zip(productName(i), productPrice(i), productReview(i))
                    .map(t -> new ProductInformation(t.getT1(), t.getT2(), t.getT3()))
                    .subscribe(product -> log.info("product received: {}", product),
                            err -> log.error("error occurred: {}", err.getMessage()),
                            () -> log.info("Completed"));
        }
        RsUtil.sleepSeconds(2);

    }

    private static Mono<String> productName(int productId) {
        return Mono.fromSupplier(() -> "product-" + productId)
                .delayElement(Duration.ofSeconds(1));
    }

    private static Mono<String> productPrice(int productId) {
        return Mono.fromSupplier(() -> "price-" + productId)
                .delayElement(Duration.ofSeconds(1));
    }

    private static Mono<String> productReview(int productId) {
        return Mono.fromSupplier(() -> "review-" + productId)
                .delayElement(Duration.ofSeconds(1));
    }
}
