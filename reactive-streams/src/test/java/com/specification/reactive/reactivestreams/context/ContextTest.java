package com.specification.reactive.reactivestreams.context;

import com.specification.reactive.reactivestreams.model.Order;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.util.context.Context;

import java.util.UUID;

/**
 * If you have used ThreadLocal in Java, you can easily relate to context in reactive programming.
 * However, unlike ThreadLocal, context is very safe to use and it's immutable in nature.
 *
 * So what is context and how it can be used?
 * So let's take a simple HTTP request. As part of the request, you send the message body right as part of your POST request.
 * And if it's a GET request, you pass the query parameters, etc.
 * So those are all fine. But what is HTTP header? What is the need for it?
 * It's additional metadata about the request you are sending, right? Same concept.
 *
 * Similarly, a subscriber can subscribe to a publisher, pass some additional information about the request, which does not have to be part of your method parameters.
 * So you attach it as something called a context, as you normally do for your HTTP request as headers.
 * By doing this way, we can handle some of the cross-cutting concerns like authentication, rate limiting, monitoring, all those stuff.
 * We can handle this separately without polluting the publisher code. So the context is like header simple key value pairs, an immutable map nothing else.
 *
 * Also, in the real life you will not be having just one single producer, one operator and one subscriber.
 * Probably you will be having multiple producers. So the producer one starting with this, producer with this operator, then you have to concatenate with another producer.
 * So you will be having multiple producers like this. And your subscriber will be subscribing to this combined or merged publisher.
 * So sometimes you will want to pass some information about the request to this producer. So instead of updating the method parameter for each and every producer or updating the existing code,
 * we can attach some additional information about your request as context as a hidden object so they all can see what you are sending as part of this context if it's required.
 *
 * Note: In short, Context is
 *  1. Provides metadata about the request (similar ot Http Request and Response Headers)
 *  2. Simple Key-Value pairs, which is an immutable Map
 *  3. Addresses cross-cutting concerns like,
 *      - Authentication
 *      - Rate Limiting
 *      - Monitoring / Tracing
 * */

@Slf4j
public class ContextTest {

    @Test
    public void context_simple_test() {
        getOrder()
                .contextWrite(Context.of("auth-token", UUID.randomUUID().toString()))
                .subscribe(RsUtil.subscriber());
    }

    /**
     * Let's imagine getOrder() is a remote API, returning mono of Order.
     * Now anybody can subscribe and get the order. Anybody can invoke get the result.
     * That is how it is. Right?
     * Suddenly the recent business requirement to secure the method. Now only authenticated users should invoke this.
     * So you think, in this case, you can simply, update method signature to get additional user information, an authentication token and check for user authentication.
     *
     * But no, we need not update our method signature. Instead, we want those session token as HTTP headers, right?
     * Similarly, we want something here without updating our method signature.
     * Reactor provides something called deferContextual. So without updating the method signature we can pass an auth-token in a context.
     * */
    private static Mono<Order> getOrder() {
//        return Mono.fromSupplier(Order::new);
        return Mono.deferContextual(ctx -> {
            log.info("context: {}", ctx.toString());
            if (ctx.hasKey("auth-token")) { // user authentication check
                Order order = new Order();
                return Mono.just(order);
            }
            return Mono.error(new RuntimeException("No auth-token found"));
        });
    }

    /**
     * Context is an immutable map. We can append additional info!
     * In Project Reactor, the Context is immutable, so you can't directly update it.
     * Instead, you create a new context with the additional or updated values using .contextWrite(...).
     * If you want to append or override values in the context, you can chain multiple .contextWrite(...) calls or use a lambda to modify the existing context.
     *
     * Note:
     *  1. Context.of(...) creates a new context with the initial key-value pair.
     *  2. .contextWrite(ctx -> ctx.put(...)) appends or overrides values in the existing context.
     *  3. deferContextual accesses the final context at subscription time.
     *
     *  Tip: Order matters.
     *  Context is applied from bottom to top (last applied is closest to the subscriber).
     *    e.g:
     *     Mono.deferContextual(ctx -> ...)
     *         .contextWrite(ctx -> ctx.put("key", "B"))
     *         .contextWrite(Context.of("key", "A"));
     *
     *  So the value of "key" will be "B".
     * */
    @Test
    public void context_append_update_test() {
        getWelcomeMessage()
                //.contextWrite(Context.empty()) // emptying the entire context
                .contextWrite(ctx -> ctx.put("user", "harry").put("a", "c")) // add or update
                .contextWrite(Context.of("a", "b")) // Initial Context
                .contextWrite(Context.of("user", "sam")) // Initial Context
                .subscribe(RsUtil.subscriber());
    }

    private static Mono<String> getWelcomeMessage() {
        return Mono.deferContextual(ctx -> {
            log.info("context: {}", ctx.toString());
            if (ctx.hasKey("user")) {
                return Mono.just("Welcome %s".formatted(ctx.get("user").toString()));
            }
            return Mono.error(new RuntimeException("Unauthenticated"));
        });
    }

    /**
     * In both the below tests, context is visible and accessible to both the publishers.
     * That's how we are able to propagate context information to all the upstream publishers.
     * */
    @Test
    public void context_propagation_one_test() {
        getWelcomeMessage()
                .concatWith(firstPublisher())
                .concatWith(secondPublisher())
                .contextWrite(Context.of("user", "sam"))
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void context_propagation_another_test() {
        getWelcomeMessage()
                .concatWith(Flux.merge(firstPublisher(), secondPublisher()))
                .contextWrite(Context.of("user", "sam"))
                .subscribe(RsUtil.subscriber());
    }

    private static Mono<String> firstPublisher() {
        return Mono.deferContextual(ctx -> {
            log.info("Context of first publisher: {}", ctx.toString());
            return Mono.empty();
        })
                .cast(String.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private static Mono<String> secondPublisher() {
        return Mono.deferContextual(ctx -> {
                    log.info("Context of second publisher: {}", ctx.toString());
                    return Mono.empty();
                })
                .cast(String.class)
                .subscribeOn(Schedulers.parallel());
    }

    @Test
    public void welcome_message_test() {
        StepVerifierOptions testOptions = StepVerifierOptions.create()
                .scenarioName("welcome-message-test-with-context")
                .withInitialContext(Context.of("user", "sam"));

        StepVerifier.create(getWelcomeMessage(), testOptions)
                .expectNext("Welcome %s".formatted("sam"))
                .expectComplete()
                .verify();
    }

    @Test
    public void welcome_message_error_condition_test() {
        StepVerifierOptions testOptions = StepVerifierOptions.create()
                .scenarioName("welcome-message-test-with-error-context")
                .withInitialContext(Context.empty());

        StepVerifier.create(getWelcomeMessage(), testOptions)
                .expectErrorMessage("Unauthenticated")
                .verify();
    }
}
