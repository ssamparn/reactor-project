package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class LimitRateOperatorTest {

    @Test
    public void limit_rate_operator_test_1() {
        Flux.range(1, 1000)
                .log()
                .limitRate(100) // first request goes for 100 items. Only 75% of the item requested gets emitted.
                                // In the next call 75% of the 100 = 75 items are requested and emitted. Subsequently, all items are emitted.
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void limit_rate_operator_test_2() {
        Flux.range(1, 1000)
                .log()
                .limitRate(100, 49) // first request goes for 100 items. Only 49% of the item requested gets emitted.
                // In the next call 49% of the 100 = 49 items are requested and emitted. Subsequently, all items are emitted.
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void limit_rate_operator_test_3() {
        Flux.range(1, 1000)
                .log()
                .limitRate(100, 100) // first request goes for 100 items. Only 75% of the item requested gets emitted.
                // In the next call 75% of the 100 = 75 items are requested and emitted. Subsequently, all items are emitted.
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void limit_rate_operator_test_4() {
        Flux.range(1, 1000)
                .log()
                .limitRate(100, 0) // first request goes for 100 items. Only 100% of the item requested gets emitted.
                // In the next call 100% of the 100 = 100 items are requested and emitted. Subsequently, all items are emitted.
                .subscribe(RsUtil.subscriber());
    }
}
