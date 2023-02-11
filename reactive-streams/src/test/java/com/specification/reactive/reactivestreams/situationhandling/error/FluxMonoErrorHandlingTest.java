package com.specification.reactive.reactivestreams.situationhandling.error;

import com.specification.reactive.reactivestreams.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
public class FluxMonoErrorHandlingTest {

    @Test
    public void flux_publisher_returns_error_test() {
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
    public void flux_publisher_returns_error_with_OnErrorResume_test() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorResume(e -> {
                    log.info("Exception is: {}", e.getMessage());
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
    public void flux_publisher_returns_error_with_OnErrorReturn_test() {
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
    public void flux_publisher_returns_error_with_OnErrorMap_test() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void flux_publisher_returns_error_with_retry_OnErrorMap_test() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
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
    public void flux_publisher_returns_error_with_retryWhen_OnErrorMap_test() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
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
