package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SwitchIfEmptyOperatorTest {

    @Test
    public void switch_if_empty_operator_test_1() {
        getOrderNumbers()
                .filter(i -> i > 10)
                .switchIfEmpty(fallBackMonoPublisher())
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void switch_if_empty_operator_test_2() {
        getOrderNumbers()
                .filter(i -> i > 10)
                .switchIfEmpty(fallBackFluxPublisher())
                .subscribe(RsUtil.subscriber());
    }

    private static Flux<Integer> getOrderNumbers() {
        return Flux.range(1, 10);
    }

    private static Mono<Integer> fallBackMonoPublisher() {
        return Mono.fromSupplier(() -> 20);
    }

    private static Flux<Integer> fallBackFluxPublisher() {
        return Flux.just(8, 10, 12, 14);
    }
}
