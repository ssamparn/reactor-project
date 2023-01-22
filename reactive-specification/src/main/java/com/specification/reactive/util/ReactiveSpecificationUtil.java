package com.specification.reactive.util;

import com.github.javafaker.Faker;
import com.specification.reactive.service.DefaultSubscriber;
import org.reactivestreams.Subscriber;

import java.util.function.Consumer;

public class ReactiveSpecificationUtil {

    private static final Faker FAKER = Faker.instance();

    public static Consumer<Object> onNext() {
        return obj -> System.out.println("Received: " + obj);
    }

    public static Consumer<Throwable> onError() {
        return err -> System.out.println("Error: " + err.getMessage());
    }

    public static Runnable onComplete() {
        return () -> System.out.println("Completed");
    }

    public static Faker faker() {
        return FAKER;
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

    public static Subscriber<Object> subscriber(String name) {
        return new DefaultSubscriber(name);
    }
}
