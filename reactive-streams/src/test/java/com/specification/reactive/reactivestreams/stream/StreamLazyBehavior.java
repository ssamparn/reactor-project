package com.specification.reactive.reactivestreams.stream;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class StreamLazyBehavior {

    /* *
     * Lazy Evaluation of Streams: Streams are lazy because intermediate operations are not evaluated until terminal operation is invoked.
     * Each intermediate operation creates a new stream, stores the provided operation / function and return the new stream.
     * The pipeline accumulates these newly created streams.
     *
     * So the behavior of the stream is lazy by default. It will not execute until we attach a terminal operator.
     * In the same concept (thought process), reactive program also works. Nothing happens until subscriber subscribes.
     * */
    @Test
    public void stream_LazyBehavior_test() {
        Stream<Integer> integerStream = Stream.of(1)
                .map(integer -> integer * 2);

        integerStream.forEach(System.out::println);
    }
}
