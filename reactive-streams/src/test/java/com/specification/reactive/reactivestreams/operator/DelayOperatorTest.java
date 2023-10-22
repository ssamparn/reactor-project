package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class DelayOperatorTest {

    @Test
    public void delay_operator_test() {
        Flux.range(1, 100)
                .log()
                .delayElements(Duration.ofSeconds(1L))
                .subscribe(RsUtil.subscriber());

        // Uncomment thread.sleep() to test the delay emission of elements behavior,
        // as delayed elements are scheduled to be emitted in a separate thread pool.

//         RsUtil.sleepSeconds(60);
    }
}
