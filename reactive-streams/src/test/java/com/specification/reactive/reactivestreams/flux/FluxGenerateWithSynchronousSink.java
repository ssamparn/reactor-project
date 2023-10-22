package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class FluxGenerateWithSynchronousSink {

    /* *
    * Flux.generate(): The generate() method of the Flux API provides a simple and straightforward programmatic approach to creating a Flux.
    * The generate method calculates and emits the values on demand.
    * When it is useful?
    * It is preferred to use in cases where it is too expensive to calculate elements that may not be used downstream.
    * It can also be used if the emitted events are influenced by the state of the application.
    * Based on its name, a SynchronousSink instance works synchronously. However, we cannot call the SynchronousSink object’s next method more than once per generator call.
    * */

    @Test
    public void flux_generate_simple_test() {
        Flux.generate(synchronousSink -> {
            log.info("Emitting");
            synchronousSink.next(RsUtil.faker().country().name()); // This will keep on emitting items if the take() is not there.
        })
        .take(2)
        .subscribe(RsUtil.subscriber());
    }

    @Test
    public void flux_generate_behaviour_test_1() {
        Flux.generate(synchronousSink -> {
            synchronousSink.next(RsUtil.faker().name().fullName());
            synchronousSink.next(RsUtil.faker().name().fullName()); // This line will result in an error.
            // With synchronousSink, only one item can be emitted. But with fluxSink we can keep on emitting items.
            synchronousSink.complete();
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_behaviour_test_2() {
        Flux.generate(synchronousSink -> {
            synchronousSink.next(RsUtil.faker().name().fullName());
            // With synchronousSink, it can emit one item only. But it's not a Mono as infinite instance(s) of synchronousSink gets created with each generate call.
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_behaviour_test_3() {
        Flux.generate(synchronousSink -> {
            synchronousSink.next(RsUtil.faker().name().fullName());
            // With synchronousSink, it can emit one item only. But it's not a Mono as infinite instance(s) of synchronousSink gets created with each generate call.
        })
        .take(5)
        .subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_behaviour_test_4() {
        Flux.generate(synchronousSink -> {
            synchronousSink.next(RsUtil.faker().name().fullName());
            synchronousSink.complete();
        })
        .take(5)
        .subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_country_names_till_canada_test() {
        Flux.generate(synchronousSink -> {
            String country = RsUtil.faker().country().name();
            log.info("Emitting Country: {}", country);
            synchronousSink.next(country);
            if (country.equalsIgnoreCase("Canada")) {
                synchronousSink.complete();
            }
        }).subscribe(RsUtil.subscriber());
    }

    @Test
    public void flux_generate_characters_test() {
        // The first argument of Flux.generate() is a Callable function.
        // This function defines the initial state for the generator with the value 97 (ascii code for 'a'). Here state works as a counter. We can also use an atomic integer instead of a state.
        // The second one is a BiFunction. This is a generator function then consumes a synchronousSink. This synchronousSink returns an item whenever the sink’s next method is invoked
        Flux.generate(() -> 97, (state, synchronousSink) -> {
            char value = (char) state.intValue(); // In the first iteration value holds 'a'
            log.info("Emitting Value: {}", value);
            synchronousSink.next(value);
            if (value == 'z') {
                synchronousSink.complete();
            }
            return state + 1;
        }).subscribe(RsUtil.subscriber());
    }

    @Test
    public void flux_generate_with_state_test() {
        Flux.generate(() -> 1, (state, synchronousSink) -> {
            String country = RsUtil.faker().country().name();
            log.info("Emitting Country: {}", country);
            synchronousSink.next(country);
            if (country.equalsIgnoreCase("Canada") || state >= 10) {
                synchronousSink.complete();
            }
            return state + 1;
        }).subscribe(RsUtil.subscriber());
    }
}
