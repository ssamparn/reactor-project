package com.specification.reactive.reactivestreams.stream;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class StreamLazyBehavior {

    /**
     * Lazy Evaluation of Streams: Streams are lazy because intermediate operations are not evaluated until terminal operation is invoked.
     * Each intermediate operation creates a new stream, stores the provided operation / function and return the new stream.
     * The pipeline accumulates these newly created streams.
     * */
    @Test
    public void stream_LazyBehavior_test() {
        Stream<Integer> integerStream = Stream.of(1)
                .map(integer -> integer * 2);

        integerStream.forEach(System.out::println);
    }
}
