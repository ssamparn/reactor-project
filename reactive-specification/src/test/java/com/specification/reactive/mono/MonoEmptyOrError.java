package com.specification.reactive.mono;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoEmptyOrError {

    @Test
    public void monoEmptyOrErrorTest() {
        userRepository(1)
                .subscribe(
                        ReactiveSpecificationUtil.onNext(),
                        ReactiveSpecificationUtil.onError(),
                        ReactiveSpecificationUtil.onComplete()
                );
    }

    private static Mono<String> userRepository(int userId) {
        if (userId == 1) {
            return Mono.just(ReactiveSpecificationUtil.faker().name().firstName());
        } else if (userId == 2) {
            return Mono.empty();
        } else
            return Mono.error(new RuntimeException("Not in the allowed range"));
    }
}
