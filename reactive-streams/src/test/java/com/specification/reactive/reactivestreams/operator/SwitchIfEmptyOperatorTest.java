package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SwitchIfEmptyOperatorTest {

    /**
     * switchIfEmpty(): and need to handle some logic based on emptiness.
     * switchIfEmpty() operator help us to do so. It switch to an alternative publisher if this sequence is completed without any data.
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
