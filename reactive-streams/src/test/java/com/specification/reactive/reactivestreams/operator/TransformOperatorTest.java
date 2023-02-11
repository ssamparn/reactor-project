package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.Person;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Locale;
import java.util.function.Function;

@Slf4j
public class TransformOperatorTest {

    @Test
    public void transform_operator_test() {
        getPerson()
                .transform(applyFilterAndMapToUpperCase())
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
                .doOnDiscard(Person.class, personDiscarded -> log.info("Discarded Object : {}", personDiscarded));
    }
}
