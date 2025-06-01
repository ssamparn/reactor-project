package com.specification.reactive.reactivestreams.publisher.flux;

public class FluxCreateWithFluxSinkUseCases {

    /* *
     * Use Case: Flux.create() -> FluxSink
     *   - It is designed to be used when we have a single subscriber.
     *   - Flux Sink is thread safe. We can share it with multiple threads.
     *   - We can keep on emitting data into sink without worrying about downstream demand.
     *   - FluxSink will deliver everything to Subscriber safely and sequentially one by one.
     *   - Don't worry about Race Conditions or data being dropped, as it is synchronized internally.
     *
     * Flux.create() is another powerful method in Project Reactor that gives you imperative control over how and when items are emitted into a Flux.
     * It’s especially useful when integrating non-reactive, callback-based, or event-driven APIs into a reactive stream.
     *
     * Flux.create() allows you to manually push data into a Flux using a FluxSink.
     * This is different from Flux.just() or Flux.defer() which are declarative.
     * e.g:
        Flux<String> flux = Flux.create(sink -> {
           sink.next("A");
           sink.next("B");
           sink.complete();
        });
     *
     * Use cases:
     *
     * 1. Bridging Callback APIs: If you have an API that uses callbacks (e.g., a listener or event handler), you can wrap it in a Flux.
     * e.g:
     *  Flux<String> flux = Flux.create(sink -> {
            someApi.setListener(data -> sink.next(data));
        });
     * This is ideal for UI events, WebSocket messages, or sensor data.
     *
     * 2. Custom Emission Logic: You can emit items based on custom logic, loops, or conditions.
     * e.g:
        Flux<Integer> flux = Flux.create(sink -> {
            for (int i = 0; i < 5; i++) {
                sink.next(i);
            }
           sink.complete();
        });
     *
     * 3. Asynchronous Data Sources: If you're integrating with an async API that doesn't return a Publisher, you can use Flux.create() to emit data as it arrives.
     *
     * 4. Backpressure Handling: Flux.create() supports backpressure strategies via FluxSink. You can specify how to handle overflow.
     * e.g:
     *  Flux.create(emitter -> {
           // emit data
        }, FluxSink.OverflowStrategy.BUFFER);
     *
     * Overflow Strategies include: BUFFER (default), DROP, LATEST, ERROR, IGNORE
     *
     * When to avoid Flux.create()?
     *   1. If you're working with already reactive sources (Mono, Flux, etc.), prefer using operators like map, flatMap, etc.
     *   2. Flux.create() gives low-level control, so it should be used carefully to avoid memory leaks or threading issues.
     * */


}
