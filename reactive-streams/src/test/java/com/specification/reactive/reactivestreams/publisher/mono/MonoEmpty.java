package com.specification.reactive.reactivestreams.publisher.mono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MonoEmpty {

    /* *
     * Mono.empty() is a method invocation that returns a Mono that completes emitting no item.
     * It represents an empty publisher that only calls onSubscribe and onComplete.
     * This Publisher is effectively stateless, and only a single instance exists.
     * */

    @Test
    public void mono_empty_test() {
        Mono<Object> emptyMono = Mono.empty();

        StepVerifier.create(emptyMono)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    /* *
    * Differences between Mono.empty() vs Mono<Void>: A Mono<T> is a specialized Publisher<T> that emits at most one item of type T.
    * Similarly the generic type Mono<T> represents Void type for Mono<Void>.
    * You can use an empty Mono<Void> to represent no-value asynchronous processes that only have the concept of completion (similar to a Runnable).
    * The Mono<Void> is a special case where Void is a class that you can never instantiate.
    *
    * Since there’s no such thing as an instance of Void, the Mono can never emit a value before its completion signal.
    * */

    @Test
    public void mono_void_test() {
        Mono<Void> thenVoidMono = Mono
                .just("Testing Mono of Void")
                .then();

        thenVoidMono.subscribe(System.out::println);
    }
}
