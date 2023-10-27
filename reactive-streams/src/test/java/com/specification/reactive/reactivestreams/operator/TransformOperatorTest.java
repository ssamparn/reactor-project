package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.Person;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.function.Function;

@Slf4j
public class TransformOperatorTest {

    /**
     * transform(): A handy operator to transform stream of events. But why transform operator if we already have a map() ?
     * Most of the engineers opt for a map operator as in when we encounter a scenario of transformation. For example, transform a person into an employee of an organisation.
     *      Mono<Employee> employeeMono = Mono.just(new Person())
     *          .map(person -> new Employee());
     * map() synchronously transforms an item from one type to another type but how about if you want to transform a publisher of type X into a publisher of type Y. Transform operator comes in handy in such cases.
     * */

    @Test
    public void transform_operator_simple_test() {
        Mono.just("first")
                .transform(upperCaseTranformer)
                .subscribe(RsUtil.subscriber());
    }

    Function<Mono<String>, Mono<String>> upperCaseTranformer =
            stringMono -> stringMono
                    .map(String::toUpperCase);

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
                .filter(person -> person.getAge() > 25)
                .doOnNext(person -> person.setName(person.getName().toUpperCase(Locale.ROOT)))
                .doOnDiscard(Person.class, personDiscarded -> log.info("Discarded persons : {}", personDiscarded));
    }

    /**
     * transformDeferred():
     *
     * */
}
