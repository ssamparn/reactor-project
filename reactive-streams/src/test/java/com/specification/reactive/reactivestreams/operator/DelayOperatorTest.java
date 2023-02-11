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

        // RsUtil.sleepSeconds(60);
        // Uncomment thread sleep to test
    }
}
