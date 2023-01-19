package com.specification.reactive.stream;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class StreamLazyBehavior {

    @Test
    public void streamLazyBehaviorTest() {
        Stream<Integer> integerStream = Stream.of(1)
                .map(integer -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return integer * 2;
                });
//        System.out.println(integerStream);
        integerStream.forEach(System.out::println);
    }
}
