package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Flux.defer(): It is used to defer/delay the creation of a Flux until a subscriber actually subscribes.
 * This is particularly useful when you want to delay the execution of some logic until subscription time, ensuring that the logic is re-evaluated for each subscriber.
 *
 * Syntax: Flux.defer(Supplier<? extends Publisher<? extends T>> supplier)
 * */

@Slf4j
public class FluxDefer {

    @Test
    public void flux_just_simple_test() {
        // The below code is responsible for creating a new Flux inside the fluxJust function and after that it’s utilized inside the fluxJustSubscription or fluxJustInstantiation methods.
        // Given that, it’s worth to quote the Reactor documentation here:
        //      Nothing Happens Until You subscribe()
        //
        // And that’s one of the most important things when dealing with Reactor - data do not start pumping into by default. By creating a Flux, we’re just defining an asynchronous process, but all the magic happens when we tie it to a Subscriber.
        // Personally, I would compare that to the process of a Java class definition – just like a java class is a simple skeleton with some predefined behavior until we create an instance, so here a Flux defines the process of data flow until we subscribe to it.
        // With that being said, we expect that a name will be printed 3 times after running fluxJustSubscription() and nothing happen in running fluxJustInstantiation().

        fluxJustSubscription();
        fluxJustInstantiation();
    }

    @Test
    public void flux_defer_simple_test() {

        // With Flux.defer(), we’ve achieved a real laziness - a successful name fetching was triggered each time a new Subscriber was registered.
        // Moreover, nothing happened, when we just instantiated a Flux. The reason behind this is pretty straightforward.
        // Flux.defer() will supply a target Flux (created by the fluxJust(), in our case) to each Subscriber.
        // In fact, the delay between each subscription could be extended to whatever value we would like to, and the printed name would be up to date each time.

        // At this point, we can clearly see that these Flux.just() and Flux.defer() methods serve different purposes.
        // If we are dealing with some constant data, or data set, or we are just OK with the data being obtained eagerly, then the Flux.just() should be the choice.
        // On the other hand, if we want the subscriber to receive data calculated at the time of subscription, then the Flux.defer() should be picked to “wrap” another Flux.

        fluxDeferSubscription();
        fluxDeferInstantiation();
    }

    public void fluxJustSubscription() {
        Flux<String> stringFlux = Flux.just(getName());

        stringFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        stringFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        stringFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }

    public void fluxDeferSubscription() {
        Flux<String> stringFlux = Flux.defer(() -> Flux.just(getName()));

        stringFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        stringFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        stringFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }

    public void fluxJustInstantiation() {
        Flux.just(getName());
    }

    public void fluxDeferInstantiation() {
        Flux.defer(() -> Flux.just(getName()));
    }

    private static String getName() {
        log.info("Producing Names: "); // As you might have noticed. The result seems to be a bit odd. Why are the printed names exactly the same in the first case and why does the "Producing Names: " has been even printed without subscription ?
        // Basically, this is the main difference between a Flux.just() and the rest of the Flux creation methods. Flux.just() is a hot publisher and the value has been captured at the instantiation time.
        RsUtil.sleepMilliSeconds(500);
        return RsUtil.faker().name().fullName();
    }

    /**
     * Flux.defer() use cases:
     *  2. Dynamic Data Fetching: Imagine you need to fetch data from a database only when the Mono is subscribed to
     *  4. Contextual Evaluation: You might want to create a Mono that depends on the security context, such as fetching roles or permissions for the current user
     *  5. Retry Mechanism: Suppose you have a method that fetches data from a remote service, and you want to retry the operation up to three times in case of failure.
     *  6. Complex Chains: Creating complex chains with Mono.defer() can help manage intricate workflows and dependencies in reactive programming.
     *     e.g: Suppose you need to perform a series of operations sequentially, with error handling at each step
     *  7. Handling Expensive Computations: You can use Mono.defer() to delay expensive computations until subscription.
     * */

    /* *
     * 1. Lazy Evaluation: If you want to ensure that a new Flux is created for each subscriber, defer() is the right choice.
     * Each subscription will get a new UUID.
     *
     * Without Flux.defer(), the UUID is generated immediately and reused for all subscribers.
     * */
    @Test
    public void flux_defer_lazy_evaluation_test() {
        Flux<String> uuidDeferedFlux = Flux.defer(() -> Flux.just(UUID.randomUUID().toString()));
        uuidDeferedFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        uuidDeferedFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        uuidDeferedFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());

        Flux<String> uuidJustFlux = Flux.just(UUID.randomUUID().toString());
        uuidJustFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        uuidJustFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        uuidJustFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }

    /* *
     * 2. Dynamic Data Sources: When the data source might change between subscriptions (e.g., reading from a file, database, or system time).
     * Each subscriber will get the current time at the moment of subscription.
     * */
    @Test
    public void flux_defer_dynamic_data_sources_test() {
        Flux<Long> currentTimeFlux = Flux.defer(() -> Flux.just(System.currentTimeMillis()));

        currentTimeFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        currentTimeFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        currentTimeFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }

    /**
     * 3. Avoiding Side Effects at Declaration Time: If creating the Flux involves side effects (e.g., logging, network calls), defer() ensures these happen only when needed.
     * e.g: Flux<String> flux = Flux.defer(() -> {
                System.out.println("Subscribed!");
                return Flux.just("Hello");
            });
     * */

    /**
     * 4. Combining with Mono/Flux Factories: When using Mono.fromCallable() or Flux.fromIterable() inside a method that should be re-evaluated per subscription.
     * e.g: Flux<String> flux = Flux.defer(() -> Flux.fromIterable(fetchDataFromDatabase()));
     * */
}
