package com.specification.reactive.reactivestreams.operator;

public class FlatMapSequentialTest {

    /* *
     * flatMapSequential(): This operator eagerly subscribes to its inner publishers like flatMap,
     * but queues up elements from later inner publishers to match the actual natural ordering and thereby prevents interleaving like concatMap.
     * This operator can be used very similar to flatMap or concatMap.
     *
     * In general, the operator is suitable for situations, where ordered non-interleaved values of concatMap with the performance advantage of flatMap is required
     * and the queue is not expected to grow infinitely.
     * */
}
