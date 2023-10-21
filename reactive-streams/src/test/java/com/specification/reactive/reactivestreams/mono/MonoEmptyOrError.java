package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoEmptyOrError {

    @Test
    public void mono_empty_or_error_test() {
        int userId = 1;
        userRepository(userId)
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
            return Mono.empty(); // returning Mono.empty() is a better way of sending an empty signal than null.
        } else
            return Mono.error(new RuntimeException("Not in the allowed range"));
    }
}
