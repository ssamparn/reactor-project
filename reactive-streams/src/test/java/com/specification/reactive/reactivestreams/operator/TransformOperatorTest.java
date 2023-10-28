package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.Person;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
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

    private final Function<Mono<String>, Mono<String>> upperCaseTranformer =
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

    private static Function<Flux<Person>, Flux<Person>> applyFilterAndMapToUpperCase() {
        return personFlux -> personFlux
                .filter(person -> person.getAge() > 25)
                .doOnNext(person -> person.setName(person.getName().toUpperCase(Locale.ROOT)))
                .doOnDiscard(Person.class, personDiscarded -> log.info("Discarded persons : {}", personDiscarded));
    }

    /**
     * The transform operator lets you encapsulate a piece of an operator chain into a function.
     * That function is applied to an original operator chain at assembly time to augment it with the encapsulated operators.
     * Doing so applies the same operations to all the subscribers of a sequence and is basically equivalent to chaining the operators directly.
     * */
    @Test
    public void transform_operator_another_test() {
        Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                .doOnNext(System.out::println)
                .transform(filterAndMapTransform)
                .subscribe(RsUtil.subscriber());
    }

    private final Function<Flux<String>, Flux<String>> filterAndMapTransform = stringFlux -> stringFlux
            .filter(color -> !color.equals("orange"))
            .map(String::toUpperCase);


    /**
     * transformDeferred(): The transformDeferred() operator is similar to transform and also lets you encapsulate operators in a function.
     * The major difference is that this function is applied to the original sequence on a per-subscriber basis.
     * It means that the function can actually produce a different operator chain for each subscription (by maintaining some state)
     * */

    public static void main(String[] args) {
        AtomicInteger ai = new AtomicInteger();
        System.out.println(ai);
        System.out.println(ai.incrementAndGet());
        System.out.println(ai.incrementAndGet());
        System.out.println(ai.addAndGet(1));
    }
    @Test
    public void transform_deferred_operator_simple_test() {
        AtomicInteger ai = new AtomicInteger();

        Function<Flux<String>, Flux<String>> filterAndMapTransformDeferred = stringFlux -> {
            if (ai.incrementAndGet() == 1) {
                return stringFlux.filter(color -> !color.equals("green"))
                        .map(String::toUpperCase);
            }
            else return stringFlux.filter(color -> !color.equals("purple"))
                        .map(String::toUpperCase);
        };

        Flux<String> composedFlux = Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                .doOnNext(System.out::println)
                .transformDeferred(filterAndMapTransformDeferred);

        composedFlux.subscribe(RsUtil.subscriber("Subscriber 1")); // ai = 1, hence [BLUE, ORANGE, PURPLE] will be received.
        composedFlux.subscribe(RsUtil.subscriber("Subscriber 2")); // ai = 2 in the next iteration, hence [BLUE, GREEN, ORANGE] will be received.
        composedFlux.subscribe(RsUtil.subscriber("Subscriber 3")); // ai = 2 in the next iteration as well, hence [BLUE, GREEN, ORANGE] will be received.
    }
}
