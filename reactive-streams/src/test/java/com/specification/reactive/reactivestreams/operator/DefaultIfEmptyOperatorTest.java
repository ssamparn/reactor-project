package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class DefaultIfEmptyOperatorTest {

    /* *
     * defaultIfEmpty(): defaultIfEmpty() is closely similar to switchIfEmpty(). It indicates the completed the sequence.
     * It uses to provide a default unique value if this sequence is completed without any data.
     * Another difference is switchIfEmpty() takes a Flux/Mono stream as input but defaultIfEmpty() takes a raw value.
     */

    @Test
    public void default_if_empty_operator_test() {
        getOrderNumbers()
                .filter(i -> i > 10) // nothing satisfies the condition so default value will be returned.
                .defaultIfEmpty(20)
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void default_if_empty_operator_another_test() {
        getOrderNumbers()
                .filter(i -> i > 8)
                .defaultIfEmpty(20)
                .subscribe(RsUtil.subscriber());
    }

    private static Flux<Integer> getOrderNumbers() {
        return Flux.range(1, 10);
    }
}
