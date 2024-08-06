package com.specification.reactive.reactivestreams.publisher.flux;

public class FluxCreateWithFluxSinkUseCases {

    /* *
     * Use Case: Flux.create() -> FluxSink
     *   - It is designed to be used when we have a single subscriber.
     *   - Flux Sink is thread safe. We can share it with multiple threads.
     *   - We can keep n emitting data into sink without worrying about downstream demand.
     *   - FluxSink will deliver everything to Subscriber safely and sequentially one by one.
     *   - Don't worry about Race Conditions or data being dropped, as it is synchronized internally.
     * */


}
