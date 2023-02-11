package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class TakeOperatorTest {

    @Test
    public void fluxTakeTest() {
        Flux.range(1, 10)
                .log()
                .take(7) // After the 7th item emitted, the subscription gets cancelled. A complete signal gets issued to the downstream.
                .log()
                .subscribe(RsUtil.subscriber());
    }
}
