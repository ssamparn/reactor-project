package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class HandleOperatorTest {

    /* *
     * handle(): map() + filter()
     * The handle() is an instance method, meaning that it is chained on an existing source (as are the common operators). It is present in both Mono and Flux.
     * It is close to generate, in the sense that it uses a SynchronousSink and only allows one-by-one emissions. However, handle can be used to generate an arbitrary value out of each source element, possibly skipping some elements.
     * In this way, it can serve as a combination of map and filter.
     */

    @Test
    public void handle_operator_test() {
        Flux.range(1, 20)
                .handle((integer, synchronousSink) -> {
                    if (integer % 2 == 0) {
                        synchronousSink.next(integer); // behaves like a filter()
                    } else {
                        synchronousSink.next(integer + "a"); // behaves like a map()
                    }
                })
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void emit_country_names_till_canada_with_handle_operator_test() {
        Flux.generate(synchronousSink -> synchronousSink.next(RsUtil.faker().country().name()))
            .map(Objects::toString)
            .handle((country, synchronousSink) -> {
                synchronousSink.next(country);
                if (country.equalsIgnoreCase("canada")) {
                    synchronousSink.complete(); // filter
                }
            })
            .subscribe(RsUtil.subscriber());
    }

    // Let’s consider an example.
    // The reactive streams specification disallows null values in a sequence.
    // What if you want to perform a map operation, but you want to use a preexisting method as the map function, and that method sometimes returns null?

    public String alphabet(int letterNumber) {
        if (letterNumber < 1 || letterNumber > 26) {
            return null;
        }
        int letterIndexAscii = 'A' + letterNumber - 1;
        return "" + (char) letterIndexAscii;
    }

    // We can then use handle to remove any nulls.

    @Test
    public void handle_operator_filter_null_values() {
        Flux.just(-1, 30, 13, 9, 20)
            .handle((i, synchronousSink) -> {
                String letter = alphabet(i);
                if (letter != null) {
                    synchronousSink.next(letter);
                }
            }).subscribe(RsUtil.subscriber());
    }
}
