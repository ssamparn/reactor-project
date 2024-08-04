package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Use Flux.from(Mono) to convert a Mono to a Flux.
 * Use Flux.next() to convert a Flux to a Mono.
 * Use Mono.from(Flux) to convert a Flux to a Mono.

 * Use case: We might use an external library which returns a Mono, but we need the data as a Flux.
 * */
public class FluxFromMonoPublisher {

    @Test
    public void flux_from_mono_publisher_test() {
        Mono<String> stringMono = Mono.just("a");

        Flux<String> stringFlux = Flux.from(stringMono);

        stringFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }

    @Test
    public void flux_to_mono_publisher_test() {
        Flux<Integer> integerFlux = Flux.range(1, 10);
        Mono<Integer> integerMono = integerFlux
                .filter(item -> item > 3)
                .next();  // next() will return a Mono<Integer> from a FLux<Integer>

        integerMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }

    @Test
    public void mono_to_flux_usecase_test() {
        Mono<String> stringMono = getUserName(1);
        Flux<String> stringFlux = Flux.from(stringMono);

        save(stringFlux);
    }

    // e.g: external service: we get username from this service
    private static Mono<String> getUserName(int userId) {
        return switch (userId) {
            case 1 -> Mono.just("sam");
            case 2 -> Mono.empty();
            default -> Mono.error(new RuntimeException("Invalid input"));
        };
    }

    // e.g: library method of any database which saves user information
    private static void save(Flux<String> stringFlux) {
        stringFlux.subscribe(RsUtil.onNext());
    }
}
