package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class HandleOperatorTest {

    /* *
     * handle(): It behaves like map() + filter(). It is a powerful operator on Flux and Mono that allows you to combine filtering and mapping logic in a single step.
     * It’s often used when you need conditional emission or custom transformation of elements.
     *
     * The handle() is an instance method, meaning that it is chained on an existing source (Flux or Mono), which is present in both Mono and Flux.
     * It is very close to Flux.generate(), in the sense that it (handle() operator) uses a SynchronousSink and only allows one-by-one emissions.
     * However, handle can be used to generate an arbitrary value out of each source element, possibly skipping some elements.
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
        Flux.<String>generate(synchronousSink -> {
            String country = RsUtil.faker().country().name();
            synchronousSink.next(country);
        })
        .handle((item, synchronousSink) -> {
            synchronousSink.next(item);
            if (item.equalsIgnoreCase("canada")) {
                synchronousSink.complete(); // filter
            }
        })
        .subscribe(RsUtil.subscriber("Country Subscriber"));
    }

    /**
     * Let’s consider an example.
     * The reactive streams specification disallows null values in a sequence.
     * What if you want to perform a map operation, but you want to use a preexisting method as the map function, and that method sometimes returns null?
     * */
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

    /* Let's consider a weird requirement *
     * 1 => -2
     * 4 => do not do anything
     * 7 => error
     * everything else => send as it is
     * */
    @Test
    public void handle_another_test() {
        Flux.range(1, 10)
//                .filter(num -> num != 7)
                .handle(((num, synchronousSink) -> {
                    switch (num) {
                        case 1 -> synchronousSink.next(-2);
                        case 4 -> {}
                        case 7 -> synchronousSink.error(new RuntimeException("error"));
                        default -> synchronousSink.next(num);
                    }
                }))
                .cast(Integer.class)
                .subscribe(RsUtil.subscriber("handle operator subscriber"));
    }
}
