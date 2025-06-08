package com.specification.reactive.reactivestreams.util;

import com.github.javafaker.Faker;
import com.specification.reactive.reactivestreams.subscriber.DefaultSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@Slf4j
public class RsUtil {

    private static final Faker FAKER = Faker.instance();

    public static Consumer<Object> onNext() {
        return obj -> log.info("Subscriber Received : {}", obj);
    }

    public static Consumer<Throwable> onError() {
        return err -> log.info("Error message during subscription: {}", err.getMessage());
    }

    public static Runnable onComplete() {
        return () -> log.info("Subscription Completed!");
    }

    public static Faker faker() {
        return FAKER;
    }

    public static void sleepMilliSeconds(int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static <T> Subscriber<T> subscriber() {
        return new DefaultSubscriber<>();
    }

    public static <T> Subscriber<T> subscriber(String subscriberName) {
        return new DefaultSubscriber<>(subscriberName);
    }

    public static <T> UnaryOperator<Flux<T>> addDebugger() {
        return flux -> flux
                .doOnNext(item -> log.info("items: {}", item))
                .doOnComplete(() -> log.info("completed"))
                .doOnError(error -> log.error("error: {}", error.getMessage()));
    }

    public static <T> UnaryOperator<Flux<T>> addDebugger(String subscriberName) {
        return flux -> flux
                .doOnSubscribe(item -> log.info("subscribing to: {}", subscriberName))
                .doOnComplete(() -> log.info("{} completed", subscriberName))
                .doOnError(error -> log.error("error: {}", error.getMessage()))
                .doOnCancel(() -> log.info("cancelling subscription {}", subscriberName));
    }
}
