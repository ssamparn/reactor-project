package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class TimeOutOperatorTest {

    /**
     * timeout():
     *  Use case: We have a requirement that we should wait for 200ms and otherwise request will time out after 200ms.
     *
     *  V.Imp Note: We can have multiple timeouts. The closest one to the subscriber will take effect for the subscriber.
     * */
    @Test
    public void time_out_operator_test() {
        getOrderItems()
                .timeout(Duration.ofMillis(200))
                .subscribe(RsUtil.subscriber()); // we will see error: Error Thrown : Did not observe any item or terminal signal within 200ms

        RsUtil.sleepSeconds(1);
    }

    // Assume getOrderItems is a simple order service which emits items every second.
    private Flux<String> getOrderItems() {
        return Flux.range(1, 30)
                .map(i -> "order from actual order service: " + i)
                .delayElements(Duration.ofSeconds(1));
    }

    @Test
    public void time_out_operator_fallback_test() {
        getOrderItems()
                .timeout(Duration.ofMillis(50), getOrderItemsFallback()) // it will wait for 50ms and if it did not receive any order, it will go to getOrderItemsFallback().
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(5);
    }

    private Flux<String> getOrderItemsFallback() {
        return Flux.range(1, 10)
                .map(i -> "order from fallback implementation: " + i)
                .delayElements(Duration.ofMillis(100));
    }
}
