package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

/* *
 * defaultIfEmpty(): Similar to error handling, defaultIfEmpty() provides solutions to handle empty values from publisher.
 * It is closely similar to switchIfEmpty(). It indicates the completed the sequence.
 * It is used to provide a default unique value if the reactive sequence is completed without any data.
 *
 * Difference between defaultIfEmpty() & switchIfEmpty() is, switchIfEmpty() takes in a publisher implementation (Flux/Mono) as input but defaultIfEmpty() takes in a raw value.
 *
 * V Imp Note: Behavior of defaultIfEmpty() while handling empty values is same as behavior of onErrorReturn() while handling errors.
 */
@Slf4j
public class DefaultIfEmptyOperatorTest {

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
