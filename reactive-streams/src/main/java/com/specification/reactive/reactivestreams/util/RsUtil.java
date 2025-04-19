package com.specification.reactive.reactivestreams.util;

import com.github.javafaker.Faker;
import com.specification.reactive.reactivestreams.subscriber.DefaultSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;

import java.util.function.Consumer;

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
}
