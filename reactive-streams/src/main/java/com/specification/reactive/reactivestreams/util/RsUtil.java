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
        return obj -> log.info("Received: {}", obj);
    }

    public static Consumer<Throwable> onError() {
        return err -> log.info("Error: {}", err.getMessage());
    }

    public static Runnable onComplete() {
        return () -> log.info("Completed");
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
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static Subscriber<Object> subscriber() {
        return new DefaultSubscriber();
    }

    public static Subscriber<Object> subscriber(String value) {
        return new DefaultSubscriber(value);
    }
}
