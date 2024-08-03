package com.nonblocking.http.client.reactorclient.util;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class Util {

    public static Consumer<Object> onNext() {
        return obj -> log.info("Received: {}", obj);
    }

    public static Consumer<Throwable> onError() {
        return err -> log.info("Error message: {}", err.getMessage());
    }

    public static Runnable onComplete() {
        return () -> log.info("Completed");
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
}