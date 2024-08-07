package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class DelayOperatorTest {

    @Test
    public void delay_operator_with_flux_range_test() {
        Flux.range(1, 20)
                .log()
                .delayElements(Duration.ofMillis(100)) // subscriber requests 1 item request(1), in each duration to the publisher. It's not like publisher emits all items at once, and it gets buffered in the queue.
                .subscribe(RsUtil.subscriber());

        // Uncomment sleepSeconds() to test the delay emission of elements behavior,
        // as delayed elements are scheduled to be emitted in a separate parallel thread pool just like Flux.interval()
        RsUtil.sleepSeconds(5);
    }

    @Test
    public void delay_operator_with_flux_interval_test() {
        Flux.interval(Duration.ofMillis(100))
                .log()
                .take(20)
                .subscribe(RsUtil.subscriber());

        // Uncomment sleepSeconds() to test the delay emission of elements behavior,
        // as delayed elements are scheduled to be emitted in a separate parallel thread pool just like Flux.delayElements()
        RsUtil.sleepSeconds(5);
    }
}
