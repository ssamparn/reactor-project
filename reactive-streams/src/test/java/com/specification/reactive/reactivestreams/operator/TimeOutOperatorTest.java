package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class TimeOutOperatorTest {

    @Test
    public void time_out_operator_test_1() {
        getOrderItems()
                .timeout(Duration.ofMillis(200))
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(1);
    }

    private Flux<Integer> getOrderItems() {
        return Flux.range(1, 30)
                .delayElements(Duration.ofMillis(50));
    }

    @Test
    public void time_out_operator_test_2() {
        getOrderItems()
                .timeout(Duration.ofSeconds(2), getOrderItems_fallback())
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(5);
    }

    private Flux<Integer> getOrderItems_fallback() {
        return Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(5));
    }
}
