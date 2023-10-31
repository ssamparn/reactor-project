package com.specification.reactive.reactivestreams.operator;

public class ConcatMapOperatorTest {

    /* *
     * concatMap(): The concatMap operator is actually quite similar to the previous one, except that the operator waits for its inner publishers to complete before subscribing to the next one.
     * In comparison to flatMap() one must admit that concatMap() is potentially less performant.
     * Whereas with flatMap the total runtime mainly depends on the slowest publisher because of its eager subscription to inner publishers.
     * concatMap() waits for its inner publisher to finish before continuing with the next item.
     * As a compromise between flatMap() and concatMap(), the operator flatMapSequential() exists.
     * */
}
