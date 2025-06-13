package com.specification.reactive.reactivestreams.publisher.mono;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * To write a simple test using StepVerifier.
 * StepVerifier acts like a subscriber.
 * */
@Slf4j
public class MonoTest {

    @Test
    public void mono_simple_test() {
        Mono<String> stringMono = Mono.just("Spring")
                .log();

        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void mono_product_test() {
        var scenarioName = StepVerifierOptions.create().scenarioName("mono-product-test");

        StepVerifier.create(getProduct(1), scenarioName)
                .expectNext("product-1")
                .expectComplete()
                .verify(); // subscribe
    }

    @Test
    public void mono_error_test() {
        StepVerifier.create(getUserName(3))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void mono_error_message_test() {
        StepVerifier.create(getUserName(3))
                .expectErrorMessage("Invalid Input")
                .verify();
    }

    @Test
    public void mono_error_and_error_message_test() {
        StepVerifier.create(getUserName(3))
                .consumeErrorWith(ex -> {
                    assertInstanceOf(RuntimeException.class, ex);
                    assertEquals("Invalid Input", ex.getMessage());
                })
                .verify();
    }

    // assume this is a method from your service class
    private Mono<String> getProduct(int productId) {
        return Mono.fromSupplier(() -> "product-" + productId)
                .doFirst(() -> log.info("invoked"));
    }

    private Mono<String> getUserName(int userId) {
        return switch (userId) {
            case 1 -> Mono.just("Sam");
            case 2 -> Mono.empty();
            default -> Mono.error(new RuntimeException("Invalid Input"));
        };
    }
}
