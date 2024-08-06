package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

/* *
 * To create a flux & emit items programmatically
 * */

@Slf4j
public class FluxGenerateWithSynchronousSink {

    /* *
    * Flux.generate(): The generate() method of the Flux accepts a consumer of SynchronousSink provides a simple and straightforward programmatic approach to creating a Flux.
    * The generate method calculates and emits the values on demand.
    * When it is useful?
    *  - It is preferred to use in cases where it is too expensive to calculate elements that may not be used downstream.
    *  - It can also be used if the emitted events are influenced by the state of the application.
    * Based on its name, a SynchronousSink instance works synchronously. However, we cannot call the SynchronousSink object’s next method more than once per generator call.
    *
    * It invokes the SynchronousSink lambda expression again and again based on subscription request/demand/requirement.
    * It can emit only 1 item at a time.
    * Will stop when synchronousSink.complete() is invoked.
    * Will stop when synchronousSink.error() is invoked.
    * Will stop if subscription gets cancelled.
    * */

    /* *
     * Difference between Flux.create() and Flux.generate():
     *  - In Flux.create(), we had the the control of for loop & we emitted items in loop.
     *  - In Flux.generate(), the reactor is in control of infinite loop (Long.MAX_VALUE), and it will keep on emitting items synchronously until subscription is cancelled or completed() or error() are invoked.
     * */

    @Test
    public void flux_generate_simple_test() {
        Flux.generate(synchronousSink -> {
            log.info("Emitting Events");
            synchronousSink.next(RsUtil.faker().country().name()); // This will keep on emitting items if the take() is not there.
        })
        .take(10)
        .subscribe(RsUtil.subscriber());
    }

    @Test
    public void flux_generate_behaviour_test_1() {
        Flux.generate(synchronousSink -> {
            log.info("Emitting Events");
            synchronousSink.next(RsUtil.faker().name().fullName());
            synchronousSink.next(RsUtil.faker().name().fullName()); // This line will result in an error, as with synchronousSink, only one item can be emitted at a time infinitely.
            synchronousSink.complete();
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_behaviour_test_2() {
        Flux.generate(synchronousSink -> {
            log.info("Emitting Events");
            synchronousSink.next(RsUtil.faker().name().fullName());
            // With synchronousSink, it can emit one item at a time infinitely. But understand that it's not a Mono of infinite instance(s) of synchronousSink. SynchronousSink gets created with each generate call.
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_behaviour_test_3() {
        Flux.generate(synchronousSink -> {
            synchronousSink.next(RsUtil.faker().name().fullName());
            synchronousSink.complete();
        })
        .take(5)
        .subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_behaviour_test_4() {
        Flux.generate(synchronousSink -> {
            synchronousSink.next(RsUtil.faker().name().fullName());
            synchronousSink.error(new RuntimeException("Error while emitting events"));
        })
        .take(5)
        .subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_country_names_till_canada_using_if_condition_test() {
        Flux.generate(synchronousSink -> {
            String country = RsUtil.faker().country().name();
            log.info("Emitting Country: {}", country);
            synchronousSink.next(country);
            if (country.equalsIgnoreCase("Canada")) {
                synchronousSink.complete();
            }
        }).subscribe(RsUtil.subscriber("Country Subscriber"));
    }

    @Test
    public void flux_generate_country_names_till_canada_using_emit_until_test() {
        Flux.<String>generate(synchronousSink -> {
            String country = RsUtil.faker().country().name();
            log.info("Emitting Country: {}", country);
            synchronousSink.next(country);
        })
        .takeUntil(countryName -> countryName.equalsIgnoreCase("CANADA"))
        .subscribe(RsUtil.subscriber("Country Subscriber"));
    }

    /* *
     * Demo flux event generation with having a state with a stateSupplier.
     *
     *  Flux.generate(
     *      () -> someObjectInitialValue,   // invoked once
     *      (someObject, synchronousSink) -> {
     *          .....
     *          .....
     *          return someObject;
     *      }
     *      someObject -> close   // invoked once
     *  )
     */


    @Test
    public void flux_generate_country_names_till_canada_but_requirement_is_donot_emit_more_than_10_countries_test() {
        Flux.generate(() -> 1, (state, synchronousSink) -> {
            String country = RsUtil.faker().country().name();
            log.info("Emitting Country: {}", country);
            synchronousSink.next(country); // emitting maximum 10 countries
            if (country.equalsIgnoreCase("Canada") || state >= 10) {
                synchronousSink.complete();
            }
            return state + 1;
        }).subscribe(RsUtil.subscriber()); // here we can also use a take() but we need more control as a producer when to emit, stop or cancel.
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


}
