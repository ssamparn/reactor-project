package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SwitchIfEmptyOperatorTest {

    /**
     * switchIfEmpty(): Similar to error handling, switchIfEmpty() provides solutions to handle empty values from publisher.
     * It needs to handle some logic based on emptiness.
     * It switches to an alternative (fallback) publisher if the subscriber received an empty signal (reactive sequence is completed without any data) from the primary publisher.
     *
     * Difference between defaultIfEmpty() & switchIfEmpty() is, switchIfEmpty() takes in a publisher implementation (Flux/Mono) as input but defaultIfEmpty() takes in a raw value.
     *
     * V Imp Note: Behavior of switchIfEmpty() while handling empty values is same as behavior of onErrorResume() while handling errors.
     *
     * Use case: First query database, if the result is empty then get values from redis cache.
     * */

    @Test
    public void switch_if_empty_operator_test() {
        getOrderNumbers()
                .filter(i -> i > 10)
                .switchIfEmpty(fallBackMonoPublisher())
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void switch_if_empty_operator_another_test() {
        getOrderNumbers()
                .filter(i -> i > 10)
                .switchIfEmpty(fallBackFluxPublisher())
                .subscribe(RsUtil.subscriber());
    }

    // cache
    private static Flux<Integer> getOrderNumbers() {
        return Flux.range(1, 10);
    }

    // db
    private static Mono<Integer> fallBackMonoPublisher() {
        return Mono.fromSupplier(() -> 20);
    }

    // db
    private static Flux<Integer> fallBackFluxPublisher() {
        return Flux.just(12, 14, 16, 18, 20);
    }
}
