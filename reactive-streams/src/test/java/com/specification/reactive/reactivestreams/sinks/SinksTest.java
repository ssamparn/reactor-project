package com.specification.reactive.reactivestreams.sinks;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinksTest {

    @Test
    void sink_replay_all_test() {
        Sinks.Many<Integer> integerSink = Sinks.many().replay().all();

        integerSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        integerSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = integerSink.asFlux();
        integerFlux1.subscribe(System.out::println);

        Flux<Integer> integerFlux2 = integerSink.asFlux();
        integerFlux2.subscribe(System.out::println);

        integerSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Test
    void sink_multicast_test() {
        Sinks.Many<Integer> integerSink = Sinks.many().multicast().onBackpressureBuffer();

        integerSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        integerSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = integerSink.asFlux();
        integerFlux1.subscribe(System.out::println);

        Flux<Integer> integerFlux2 = integerSink.asFlux();
        integerFlux2.subscribe(System.out::println);

        integerSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Test
    void sink_unicast_test() {
        Sinks.Many<Integer> integerSink = Sinks.many().unicast().onBackpressureBuffer();

        integerSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        integerSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = integerSink.asFlux();
        integerFlux1.subscribe(System.out::println);

        Flux<Integer> integerFlux2 = integerSink.asFlux();
        integerFlux2.subscribe(System.out::println);

        integerSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
