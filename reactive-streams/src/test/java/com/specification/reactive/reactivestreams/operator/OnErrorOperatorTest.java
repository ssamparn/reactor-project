package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OnErrorOperatorTest {

    @Test
    public void on_error_return_test() {
        Flux.range(1, 10)
                .log()
                .map(i -> 20 / (5 - i))
                .onErrorReturn(20) // the pipeline stops after the error and a cancel() event gets emitted.
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_resume_test() {
        Flux.range(1, 10)
                .log()
                .map(i -> 20 / (5 - i))
                .onErrorResume(e -> fallBack()) // the pipeline stops after the error and a cancel() event gets emitted.
                // Here we are not using the exception object e. We are simply ignoring it and return a value from fallback().
                .subscribe(RsUtil.subscriber());
    }

    // In both onErrorReturn() and onErrorResume() the pipeline stops after the error and a cancel() event gets emitted. But what if you want to continue the event emission.
    // That's when we should use onErrorContinue()
    @Test
    public void on_error_continue_test() {
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
