package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * take(): In Project Reactor, the take() operator is used to limit the number of items emitted by a Flux or Mono.
 * It’s a filtering operator that allows you to take only the first N elements or elements within a time window.
 * It is also useful when you want to cancel a long-running or infinite stream after a certain number of items or time.
 * If you want to preview the first few items of a stream (e.g., for logging or testing), then also take() is useful.
 * */
public class TakeOperatorTest {

    @Test
    public void flux_take_operator_number_of_items_test() {
        Flux.range(1, 10)
                .log()
                .take(7) // After the 7th item emitted, the subscription gets cancelled. A complete signal gets issued to the downstream.
                .log()
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void flux_take_operator_items_in_a_duration_test() {
        Flux.range(1, 1000)
                .map(i -> RsUtil.faker().country().name())
                .take(Duration.ofMillis(25)) // This emits 1000 country names, but only the names emitted within 25 ms will be consumed.
                .subscribe(RsUtil.subscriber());
    }
}
