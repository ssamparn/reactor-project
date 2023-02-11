package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OnErrorOperatorTest {

    @Test
    public void on_error_operator_test_1() {
        Flux.range(1, 10)
                .log()
                .map(i -> 20 / (5 - i))
                .onErrorReturn(20) // the pipeline stops after the error. A cancel() event gets emitted
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_operator_test_2() {
        Flux.range(1, 10)
                .log()
                .map(i -> 20 / (5 - i))
                .onErrorResume(e -> fallBack()) // the pipeline stops after the error. A cancel() event gets emitted
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_operator_test_3() {
        Flux.range(1, 10)
                .log()
                .map(i -> 20 / (5 - i))
                .onErrorContinue((err, obj) -> {

                }) // the pipeline gets continued after the error.
                .subscribe(RsUtil.subscriber());
    }

    private static Mono<Integer> fallBack() {
        return Mono.fromSupplier(() -> RsUtil.faker().random().nextInt(100, 200));
    }
}
