package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.Person;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/* *
 * transform(): In Project Reactor, the transform() operator is a powerful method that allows you to encapsulate reusable transformation logic for a Flux or Mono.
 * It takes in a function that receives a Publisher (like a Flux or Mono) and returns a transformed Publisher (again a transformed Flux or Mono)
 * It is a handy operator to transform the stream of events. But why transform operator if we already have a map() ?
 * Most of the engineers opt for a map operator as in when we encounter a scenario of transformation. For example, transform a person into an employee of an organisation.
 *      Mono<Employee> employeeMono = Mono.just(new Person())
 *          .map(person -> new Employee());
 * map() synchronously transforms an item from one type to another type but how about if you want to transform a publisher of type X into a publisher of type Y (can be same type (X) also). Transform operator comes in handy in such cases.
 *
 * Note: We have a transform() operator on both Flux and Mono flavors
 * */

@Slf4j
public class TransformOperatorTest {

    @Test
    public void transform_operator_simple_test() {
        Mono.just("first")
                .transform(lengthTransformer)
                .subscribe(RsUtil.subscriber());
    }

    private final Function<Mono<String>, Mono<Integer>> lengthTransformer = stringMono -> stringMono.map(String::length);

    @Test
    public void transform_operator_test() {
        getPerson()
                //.transform(applyFilterAndMapToUpperCaseWithFunction())
                .transform(applyFilterAndMapToUpperCaseWithFunctionWithUnaryOperator())
                .subscribe(RsUtil.subscriber());
    }

    private static Flux<Person> getPerson() {
        return Flux.range(1, 10)
                .map(i -> new Person());
    }

    private static Function<Flux<Person>, Flux<Person>> applyFilterAndMapToUpperCaseWithFunction() {
        return personFlux -> personFlux
                .filter(person -> person.getAge() > 25)
                .doOnNext(person -> person.setName(person.getName().toUpperCase(Locale.ROOT)))
                .doOnDiscard(Person.class, personDiscarded -> log.info("Discarded persons : {}", personDiscarded)); // discarded persons will have age lesser than 25.
    }

    /* *
     * We can use a unary operator here as we have a function with same input and output type.
     * public interface UnaryOperator<T> extends Function<T, T> {
     *
     * }
     */
    private static UnaryOperator<Flux<Person>> applyFilterAndMapToUpperCaseWithFunctionWithUnaryOperator() {
        return personFlux -> personFlux
                .filter(person -> person.getAge() > 25)
                .doOnNext(person -> person.setName(person.getName().toUpperCase(Locale.ROOT)))
                .doOnDiscard(Person.class, personDiscarded -> log.info("Discarded persons : {}", personDiscarded)); // discarded persons will have age lesser than 25.
    }

    /* *
     * The transform() operator lets you encapsulate a piece of an operator chain into a function.
     * That function is applied to an original operator chain at assembly time to augment it with the encapsulated operators.
     * Doing so applies the same operations to all the subscribers of a sequence and is basically equivalent to chaining the operators directly.
     * */
    @Test
    public void transform_operator_another_test() {
        Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                .doOnNext(item -> log.info("items: {}", item))
                .transform(filterAndMapTransform)
                .subscribe(RsUtil.subscriber());
    }

    private final UnaryOperator<Flux<String>> filterAndMapTransform = stringFlux -> stringFlux
            .filter(color -> !color.equals("orange"))
            .map(String::toUpperCase)
            .doOnDiscard(String.class, itemDiscarded -> log.info("Discarded items : {}", itemDiscarded));


    record Customer(int id, String name) {}
    record PurchaseOrder(String productName, int price, int quantity) {}
    boolean isDebugEnabled = true;

    private static Flux<Customer> getCustomers() {
        return Flux.range(1, 3)
                .map(i -> new Customer(i, RsUtil.faker().name().firstName()));
    }

    private static Flux<PurchaseOrder> getPurchaseOrders() {
        return Flux.range(1, 5)
                .map(i -> new PurchaseOrder(RsUtil.faker().commerce().productName(), i, i * 10));
    }

    @Test
    public void add_debugger_with_transform_operator() {
        getCustomers()
                .transform(isDebugEnabled ? addDebugger() : Function.identity())
                .subscribe(RsUtil.subscriber());

        getPurchaseOrders()
                .transform(addDebugger())
                .subscribe(RsUtil.subscriber());
    }

    private <T> UnaryOperator<Flux<T>> addDebugger() {
        return flux -> flux
                .doOnNext(item -> log.info("items: {}", item))
                .doOnComplete(() -> log.info("completed"))
                .doOnError(error -> log.error("error: {}", error.getMessage()));
    }


    /* *
     * V. Imp Note: Here after transforming we still can not get a Flux<String>. Since we provided a Mono, we will get a Mono back.
     * */
    @Test
    public void transformMonoListPublisher_to_FluxTypePublisher() {
        Mono.just(List.of("one", "two", "three", "four"))
                .transform(transformMonoToFlux) // It's a Mono<String>. Not a Flux<String>.
                .subscribe(RsUtil.subscriber());
    }

    private final Function<Mono<List<String>>, Flux<String>> transformMonoToFlux =
            stringList -> stringList.flatMapMany(Flux::fromIterable);

    /* *
     * transformDeferred(): The transformDeferred() operator is similar to transform() and also lets you encapsulate operators in a function just like transform().
     * The major difference is that this function is applied to the original sequence on a PER-SUBSCRIBER basis.
     * It means that the function can actually produce a different operator chain for EACH SUBSCRIPTION (by maintaining some state)
     * */

    @Test
    public void testBehaviorOfAtomicInteger() {
        AtomicInteger ai = new AtomicInteger(); // atomic integer starts with 0.
        log.info("integer: {}", ai);
        log.info("integer incrementAndGet: {}", ai.incrementAndGet());
        log.info("integer incrementAndGet: {}", ai.incrementAndGet());
        log.info("integer addAndGet: {}", ai.addAndGet(1));
        log.info("integer addAndGet: {}", ai.addAndGet(2));
    }

    @Test
    public void transform_deferred_operator_simple_test() {
        AtomicInteger ai = new AtomicInteger();

        Function<Flux<String>, Flux<String>> filterAndMapTransformDeferred = stringFlux -> {
            if (ai.incrementAndGet() == 1) {
                return stringFlux.filter(color -> !color.equals("green"))
                        .map(String::toUpperCase); // ["BLUE", "ORANGE", "PURPLE"]
            }
            else return stringFlux.filter(color -> !color.equals("purple"))
                        .map(String::toUpperCase); // ["BLUE", "GREEN", "ORANGE"]
        };

        Flux<String> composedFlux = Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                .doOnNext(System.out::println)
                .transformDeferred(filterAndMapTransformDeferred);

        composedFlux.subscribe(RsUtil.subscriber("Subscriber 1")); // ai = 1, hence [BLUE, ORANGE, PURPLE] will be received.
        composedFlux.subscribe(RsUtil.subscriber("Subscriber 2")); // ai = 2 in the next iteration, hence [BLUE, GREEN, ORANGE] will be received.
        composedFlux.subscribe(RsUtil.subscriber("Subscriber 3")); // ai = 3 in the next iteration as well, hence [BLUE, GREEN, ORANGE] will be received.
    }
}
