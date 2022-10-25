package com.specification.reactive.reactivestreams.errorhandling;

import com.specification.reactive.reactivestreams.exception.CustomException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxMonoErrorHandlingTest {

    @Test
    public void fluxErrorTest() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandlingTest_WithOnErrorResume() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorResume(e -> {
                    System.out.println("Exception is: " + e);
                    return Flux.just("Default Return value");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectNext("Default Return value")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingTest_WithOnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("Default Return Value");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectNext("Default Return Value")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingTest_WithOnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap( e -> new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_WithRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap( e -> new CustomException(e))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_WithRetryBackOff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap( e -> new CustomException(e))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2)));


        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectError(IllegalStateException.class)
                .verify();
    }
}
