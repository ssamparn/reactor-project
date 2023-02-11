package com.specification.reactive.reactivestreams.operator;

import com.github.javafaker.Faker;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class HandleOperatorTest {

    @Test
    public void handle_operator_test() {

        // handle = map + filter

        Flux.range(1, 20)
                .handle((integer, synchronousSink) -> {
                    if (integer % 2 == 0) {
                        synchronousSink.next(integer); // filter
                    } else {
                        synchronousSink.next(integer + "a"); // map
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
}
