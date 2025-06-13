package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

import java.util.Objects;

public class FluxTest {

    @Test
    public void flux_test() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        StepVerifier.create(integerFlux, 2) // requesting 2 events only
                .expectNext(1)
                .expectNext(2)
                .thenCancel() // cannot expect complete signal to be emitted
                .verify();
    }

    @Test
    public void flux_range_test() {
        Flux<Integer> integerFlux = Flux.range(1, 50);

        StepVerifier.create(integerFlux)
                .expectNextCount(50)
                .verifyComplete();

        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .expectNextCount(40)
                .verifyComplete();
    }

    @Test
    public void flux_range_random_integer_test() {
        Flux<Integer> integerFlux = Flux.range(1, 50)
                .map(i -> RsUtil.faker().random().nextInt(1, 100));

        StepVerifier.create(integerFlux)
                .expectNextMatches(i -> i > 0 && i < 101)
                .expectNextCount(49)
                .verifyComplete();
    }

    @Test
    public void flux_range_integer_test() {
        Flux<Integer> integerFlux = Flux.range(1, 50)
                .map(i -> RsUtil.faker().random().nextInt(1, 100));

        StepVerifier.create(integerFlux)
                .thenConsumeWhile(i -> i > 0 && i < 101)
                .verifyComplete();
    }

    @Test
    public void flux_simple_test() {

        Flux<String> springFlux = Flux.just(
                        "Spring Framework",
                        "Spring Boot",
                        "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .concatWith(Flux.just("Test to see if Flux emits")) // Note: After an error is emitted from Flux, it will not emit anymore data. So this line will not be included in the onComplete() event.
                .log();

        springFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete()); // The last Runnable will not run as well because of above reason.
    }

    @Test
    public void flux_elements_without_error_test() {
        StepVerifierOptions scenarioName = StepVerifierOptions.create().scenarioName("flux-elements-without-error-test");

        Flux<String> stringElementsFlux = Flux.just(
                        "Spring",
                        "Spring Boot",
                        "Spring Framework",
                        "Spring Reactive")
                .log();

        StepVerifier.create(stringElementsFlux, scenarioName)
                .expectNext("Spring")
                .as("element should be Spring")
                .expectNext("Spring Boot")
                .as("element should be Spring Boot")
                .expectNext("Spring Framework")
                .as("element should be Spring Framework")
                .expectNext("Spring Reactive")
                .as("element should be Spring Reactive")
        .verifyComplete();
    }

    @Test
    public void flux_elements_with_error_test() {

        Flux<String> stringElementsFlux = Flux.just(
                        "Spring Boot",
                        "Spring Framework",
                        "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .log();

        StepVerifier.create(stringElementsFlux)
                .expectNext("Spring Boot")
                .expectNext("Spring Framework")
                .expectNext("Spring Reactive")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void flux_elements_with_error_test2() {

        Flux<String> stringElementsFlux = Flux.just(
                "Spring Boot",
                "Spring Framework",
                "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .log();

        StepVerifier.create(stringElementsFlux)
                .expectNext("Spring Boot", "Spring Framework", "Spring Reactive")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void flux_elements_with_error_message_test() {

        Flux<String> stringElementsFlux = Flux.just(
                "Spring Boot",
                "Spring Framework",
                "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .log();

        StepVerifier.create(stringElementsFlux)
                .expectNext("Spring Boot")
                .expectNext("Spring Framework")
                .expectNext("Spring Reactive")
                .expectErrorMessage("Exception occured")
                .verify();
    }

    @Test
    public void flux_elements_events_count_test() {

        Flux<String> stringElementsFlux = Flux.just(
                "Spring Boot",
                "Spring Framework",
                "Spring Reactive")
                .log();

        StepVerifier.create(stringElementsFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void flux_elements_events_count_with_error_message_test() {

        Flux<String> stringFlux = Flux.just(
                "Spring",
                "Spring Boot",
                "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception Occurred")
                .verify();
    }

    /**
     * "assertNext" is a method in StepVerifier
     * assertNext = consumeNextWith
     * We can also collect all the items and test
     * */
    @Test
    public void flux_assert_next_test() {
        Flux<Book> bookFlux = Flux.range(1, 3)
                .map(i -> new Book(i, RsUtil.faker().book().author(), RsUtil.faker().book().title()));

        StepVerifier.create(bookFlux)
                .assertNext(book -> Assertions.assertEquals(1, book.id))
                .thenConsumeWhile(book -> Objects.nonNull(book.title))
                .expectComplete()
                .verify();
    }

    @Test
    public void flux_collect_all_and_test() {
        Flux<Book> bookFlux = Flux.range(1, 3)
                .map(i -> new Book(i, RsUtil.faker().book().author(), RsUtil.faker().book().title()));

        StepVerifier.create(bookFlux.collectList())
                .assertNext(bookList -> Assertions.assertEquals(3, bookList.size()))
                .expectComplete()
                .verify();
    }

    record Book(int id, String author, String title) {

    }
}