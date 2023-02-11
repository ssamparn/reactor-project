package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoEmptyOrError {

    @Test
    public void mono_empty_OrError_test() {
        userRepository(1)
                .subscribe(
                        RsUtil.onNext(),
                        RsUtil.onError(),
                        RsUtil.onComplete()
                );
    }

    private static Mono<String> userRepository(int userId) {
        if (userId == 1) {
            return Mono.just(RsUtil.faker().name().firstName());
        } else if (userId == 2) {
            return Mono.empty();
        } else
            return Mono.error(new RuntimeException("Not in the allowed range"));
    }
}
