package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.stream.IntStream;

/* *
 * Flux.take() is similar to limit() of stream.
 */

@Slf4j
public class FluxCreateWithFluxSinkIssueFix {

    @Test
    public void take_operator_with_flux_simple_test() {
        // publisher implementation
        Flux.range(1, 10)
                .log("take")
                .take(3) // take() will request for 3 items. so will issue request(3).
                .log("subscriber")
                .subscribe(RsUtil.subscriber()); // will emit 1, 2, 3. after that take() operator will issue cancel() & onComplete() will be issued by subscriber.
    }

    @Test
    public void take_while_operator_with_flux_simple_test() {
        // publisher implementation
        Flux.range(1, 20)
                .log("take-while")
                .takeWhile(num -> num < 10) // takeWhile() will stop when the condition is not met
                .log("subscriber")
                .subscribe(RsUtil.subscriber()); // will emit 1, 2, 3, 4, 5, 6, 7, 8, 9. after that takeWhile() operator will issue cancel() & onComplete() will be issued by subscriber.
    }

    @Test
    public void take_until_operator_with_flux_simple_test() {
        // publisher implementation
        Flux.range(1, 20)
                .log("take-until")
                .takeUntil(num -> num > 10) // takeUntil() will stop when the condition is met + allow the item to be emitted and received which satisfied the condition.
                .log("subscriber")
                .subscribe(RsUtil.subscriber()); // will emit 1. after that takeUntil() operator will issue cancel() & onComplete() will be issued by subscriber.
    }

    @Test
    public void take_operator_with_flux_sink_test() {
        // publisher implementation
        Flux.create(fluxSink -> {
            int i = 0;
            do {
                log.info("Emitting: {}", i);
                fluxSink.next(i);
                i++;
            } while (i <= 10);
            fluxSink.complete();
        })
        .take(3) // here subscriber is not receiving any data after 3 data. But fluxSink keeps on emitting items even after 3 items. so we have to check for the cancellation of fluxSink before emitting data.
        .subscribe(RsUtil.subscriber());
    }

    @Test
    public void take_operator_with_flux_sink_cancelled_test() {
        // publisher implementation
        Flux.create(fluxSink -> {
            int i = 0;
            do {
                log.info("Emitting: {}", i);
                fluxSink.next(i);
                i++;
            } while (i <= 10 && !fluxSink.isCancelled());
            fluxSink.complete();
        })
        .take(3)
        .subscribe(RsUtil.subscriber());
    }

    @Test
    public void flux_sink_requested_country_names_test() {
        // publisher implementation
        Flux.create(fluxSink -> {
            String country;
            do {
                country = RsUtil.faker().country().name();
                log.info("Emitting : {}", country);
                fluxSink.next(country);
            } while (!country.equalsIgnoreCase("canada") && !fluxSink.isCancelled()); // without fluxSink.isCancelled() the fluxSink will keep on emitting items despite the take operator.
            fluxSink.complete();
        })
        .take(3)
        .subscribe(RsUtil.subscriber());
    }
}
