package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class DefaultIfEmptyOperatorTest {

    @Test
    public void default_if_empty_operator_test_1() {
        getOrderNumbers()
                .filter(i -> i > 10)
                .defaultIfEmpty(20)
                .subscribe(RsUtil.subscriber());
    }

    private static Flux<Integer> getOrderNumbers() {
        return Flux.range(1, 10);
    }

    @Test
    public void default_if_empty_operator_test_2() {
        getOrderNumbers()
                .filter(i -> i > 8)
                .defaultIfEmpty(20)
                .subscribe(RsUtil.subscriber());
    }
}
