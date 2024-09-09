package com.specification.reactive.reactivestreams.repeat.n.retry;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

/* *
 * Repeat():
 * In reactive programming, we know that we have a publisher and a subscriber.
 * Publisher emits events to the subscriber. Subscriber cannot expect any events after the onComplete or onError signals.
 * But what repeat() operator does, once the onComplete signal is received, subscriber will automatically resubscribe to the publisher,
 * and request the publisher to emit the events once again.
 * So it will keep on repeating the operation based on provided configuration.
 *
 * Use case of repeat():
 * Let's imagine that we have a remote API which emits a Mono<T>.
 * Now he remote API can keep on pushing the updates to us if our API has been designed that way. But if the API is not designed that way, what would be our options?
 * So we do not have an option, right? As we have only a response Mono<T>.
 * But if we use repeat() we can keep on requesting / subscribing / calling the remote API again and again.
 * We just made it as a Flux<String>
 * */
@Slf4j
public class RepeatTest {

    @Test
    public void repeat_behavior_test() {
        Mono<String> countryMono = getCountry();
        Subscriber<String> subscriber = RsUtil.subscriber();

        //capitalMono.subscribe(subscriber);
        //capitalMono.subscribe(subscriber); // since it's a cold publisher, we can subscribe again to emit one more capital name.
        // But instead of doing it like this, we can use repeat().

        countryMono.repeat(2).subscribe(subscriber); // repeat() is resubscribing to the publisher 2 times. Hence, total number of subscriptions is 3.

        /* *
         * V.Imp Note: Here we have attached repeat() to a Mono<String>. But after attaching repeat() on a Mono<String>, it's no longer a Mono<String>.
         * It became a Flux<String>
         * */
    }

    /* *
     * Now you might think what is the big deal here? So why are we having this operator. Instead of repeating with repeat(),
     * we can create a for loop, and we can subscribe within the loop. Even that will work. We can argue that and actually the argument is true.
     * But there is a huge difference between subscribing with repeat() & subscribing with for-loop.
     * So in case of for-loop, everything seems to work is mainly because everything is in-memory.
     * But in the real life these are all going to be non-blocking I/O operations.
     * So the problem will be, even before you get the response for the first iteration, you will be running the second third iterations and so on.
     * So this is where the repeat() comes into picture. The repeat() is going to repeat only after it receives the onComplete signal.
     * So, execution happens in sequence like one by one. But if you use for-loop you are only sending concurrent requests
     * & which is not the point. That is not the expectation here.
     * So repeat() is supposed to resubscribe only after subscriber receives the onComplete signal. This is where the loop will not be helpful.
     * One more advantage is, by adding repeat() operator, we made the publisher a Flux.
     * which will automatically handle the back pressure, but with the for loop you cannot do that.
     * */
    @Test
    public void re_subscribing_with_for_loop() {
        Mono<String> countryMono = getCountry(); // In real-life, this is going to be non-blocking I/O.
        Subscriber<String> subscriber = RsUtil.subscriber();

        for (int i = 0; i < 3; i++) {
            countryMono.subscribe(subscriber);
        }
    }

    @Test
    public void repeat_based_on_condition_test() {
        getCountry()
                .repeat()
                .takeUntil(country -> country.equalsIgnoreCase("Canada"))
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void repeat_based_on_condition_another_test() {
        AtomicInteger atomicInteger = new AtomicInteger();
        BooleanSupplier booleanSupplier = () -> atomicInteger.incrementAndGet() < 3;

        getCountry()
                .repeat(booleanSupplier) // accepts a boolean supplier. BooleanSupplier sup = () -> true;
                .subscribe(RsUtil.subscriber());
    }

    /* *
     * In all these cases, subscriber has been resubscribing almost immediately after receiving onComplete signal.
     * But in some use cases, we don't have to resubscribe immediately.
     * Use cases: Imagine we are calling a remote API & we pay a huge subscription fee for every rest api call.
     * We don't have to bombard the remote API with lots of rest calls. It would be very expensive.
     * In these cases, we can repeat in some intervals.
     * */
    @Test
    public void repeat_after_a_duration_repeatWhen_test() {
        getCountry()
                .repeatWhen(longFlux -> longFlux.delayElements(Duration.ofMillis(10)))
                .takeUntil(country -> country.equalsIgnoreCase("Canada"))
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(10);
    }

    private static Mono<String> getCountry() {
        return Mono.fromSupplier(() -> RsUtil.faker().country().name());
    }

}
