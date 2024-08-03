package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

/**
 * Mono.defer(): To delay the publisher creation
 * */

@Slf4j
public class MonoDefer {

    /* *
    * Before we understand what does Mono.defer() does, we have to understand, eager and lazy evaluation, Hot & Cold Publishers.
    * In Project Reactor all Publishers (like Monos or Fluxes) are considered either one of them.
    * Cold publishers, generate new data for each subscription (consider it a bit similar to lazy evaluation),
    * whereas hot publishers are independent of subscribers and will produce data anyway (similarity to eagerness).
    *
    * In general, both eager and lazy evaluations describe the way expressions are evaluated.
    * With eager evaluation, the value will be evaluated immediately, when the instruction has been encountered.
    * With lazy evaluation, the process will be delayed until the value is really needed, hence the term - lazy evaluation.
    * */
    /* *
    * Mono.defer(): We use Mono.defer() to delay not only the execution but also the creation of a Mono publisher. So Publisher will be created if and only if it is subscribed to.
    * We can create a cold mono publisher which can produce at most one value using defer method of the Mono.
    * Mono.defer() takes in a Supplier of Mono publisher and returns that Mono lazily when subscribed downstream.
    * */

    @Test
    public void mono_just_simple_test() {
        // The below code is responsible for creating a new Mono inside the monoJust function and after that it’s utilized inside the monoJustSubscription or monoJustInstantiation methods.
        // Given that, it’s worth to quote the Reactor documentation here:
        //      Nothing Happens Until You subscribe()
        //
        // And that’s one of the most important things when dealing with Reactor - data do not start pumping into by default. By creating a Mono, we’re just defining an asynchronous process, but all the magic happens when we tie it to a Subscriber.
        // Personally, I would compare that to the process of a Java class definition – just like a java class is a simple skeleton with some predefined behavior until we create an instance, so here a Mono defines the process of data flow until we subscribe to it.
        // With that being said, we expect that a name will be printed 3 times after running monoJustSubscription() and nothing happen in running monoJustInstantiation().

        monoJustSubscription();
        monoJustInstantiation();
    }

    @Test
    public void mono_defer_simple_test() {

        // With Mono.defer(), we’ve achieved a real laziness - a successful name fetching was triggered each time a new Subscriber was registered.
        // Moreover, nothing happened, when we just instantiated a Mono. The reason behind this is pretty straightforward.
        // Mono.defer() will supply a target Mono (created by the monoJust(), in our case) to each Subscriber.
        // In fact, the delay between each subscription could be extended to whatever value we would like to, and the printed name would be up to date each time.

        // At this point, we can clearly see that these Mono.just() and Mono.defer() methods serve different purposes.
        // If we are dealing with some constant data, or data set, or we are just OK with the data being obtained eagerly, then the Mono.just() should be the choice.
        // On the other hand, if we want the subscriber to receive data calculated at the time of subscription, then the Mono.defer() should be picked to “wrap” another Mono.

        monoDeferSubscription();
        monoDeferInstantiation();
    }

    @Test
    public void mono_supplier_simple_test() {
        // Similarly to Mono.defer(), we can delay the data evaluation with Mono.fromSupplier() case.
        // As the documentation states, it allows us to:
        // Create a Mono, producing its value using the provided Supplier.
        // If the Supplier resolves to null, the resulting Mono completes empty.

        monoSupplierSubscription();
        monoSupplierInstantiation();
    }

    /* *
    * Similarity with Mono.defer() and Mono.supplier():
    * We can clearly spot that Mono.supplier() and Mono.defer() execution worked exactly the same.
    * As a word of explanation - both methods serve us to delay (defer) the moment of capturing the value.
    * Most of the time, we will lean toward them when dealing with external libraries, or another part of the code that we do not have an influence on.
    * To put it simple, our choice will be:
        Mono.defer() : When dealing with another library, method or whatever else returning a Mono instance.
        Mono.fromSupplier() : When consuming a simple value being returned from external (not a Mono)
    * */

    public void monoJustSubscription() {
        Mono<String> stringMono = Mono.just(getName());

        stringMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        stringMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        stringMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }

    public void monoDeferSubscription() {
        Mono<String> stringMono = Mono.defer(() -> Mono.just(getName()));

        stringMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        stringMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        stringMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }

    public void monoSupplierSubscription() {
        Mono<String> stringMono = Mono.fromSupplier(() -> getName());

        stringMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        stringMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        stringMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }

    public void monoJustInstantiation() {
        Mono.just(getName());
    }

    public void monoDeferInstantiation() {
        Mono.defer(() -> Mono.just(getName()));
    }

    public void monoSupplierInstantiation() {
        Mono.fromSupplier(() -> getName());
    }

    private static String getName() {
        log.info("Producing Names: "); // As you might have noticed. The result seems to be a bit odd. Why are the printed names exactly the same in the first case and why does the "Producing Names: " has been even printed without subscription ?
        // Basically, this is the main difference between a Mono.just() and the rest of the Mono creation methods. Mono.just() is a hot publisher and the value has been captured at the instantiation time.
        RsUtil.sleepMilliSeconds(500);
        return RsUtil.faker().name().fullName();
    }
}
