package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.Person;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
public class SwitchOnFirstOperator {

    /**
     * switchOnFirst(): This Flux’s method helps us to switch flux pipeline based on the first emitted value. This method will receive 2 parameters.
     *     1. First emitted signal
     *     2. The Flux itself
     *
     * It should return the publisher. It could transform the pipeline if it is required.
     * */

    @Test
    public void switch_on_operator_simple_test() {
        Flux.just(1, 2, 3, 4, 5, 6, 7)
                .defaultIfEmpty(100)
                .subscribe(RsUtil.subscriber());
        // This code will return 1 to 7 entirely. defaultIfEmpty block will not be kicked in.

        Flux.just(1, 2, 3, 4, 5, 6, 7)
                .switchOnFirst(((signal, integerFlux) -> {
                    if (signal.get() == 2) {
                        return integerFlux;
                    } else return Flux.empty();
                }))
                .defaultIfEmpty(100)
                .subscribe(RsUtil.subscriber());

        // Here switchOnFirst() is added which checks if the first emitted value is 2. If it is 2, then we will receive other elements as well.
        // If not, then I get the default value of 100 as the switchOnFirst cancels the source emission and publishes empty to downstream.

        Flux.just(1, 2, 3, 4, 5, 6, 7)
                .switchOnFirst(((signal, integerFlux) -> {
                    if (signal.get() == 1) {
                        return integerFlux;
                    } else return Flux.empty();
                }))
                .defaultIfEmpty(100)
                .subscribe(RsUtil.subscriber());

        // Here switchOnFirst() is added which checks if the first emitted value is 1. If it is 1, then we will receive other elements as well.
        // If not, then I get the default value of 100 as the switchOnFirst cancels the source emission and publishes empty to downstream.
    }


    @Test
    public void switch_on_first_operator_test() {
        getPerson()
            .switchOnFirst(((signal, personFlux) -> {
                return signal.isOnNext() && Objects.requireNonNull(signal.get()).getAge() > 18 ?
                        personFlux : applyFilterAndMapToUpperCase().apply(personFlux);
            }))
            .subscribe(RsUtil.subscriber());
    }

    private static Flux<Person> getPerson() {
        return Flux.range(1, 10)
                .map(i -> new Person());
    }

    public static Function<Flux<Person>, Flux<Person>> applyFilterAndMapToUpperCase() {
        return personFlux -> personFlux
            .filter(person -> person.getAge() > 20)
            .doOnNext(person -> person.setName(person.getName().toUpperCase(Locale.ROOT)))
            .doOnDiscard(Person.class, personDiscarded -> log.info("Discarded persons : {}", personDiscarded));
    }
}
