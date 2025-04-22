package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoEmptyOrError {

    /* *
     * Emitting empty() or error()
     * */

    @Test
    public void mono_empty_or_error_test() {
        int userId = 3;
        userRepository(userId)
                .subscribe(
                        RsUtil.onNext(),
                        RsUtil.onError(),
                        RsUtil.onComplete()
                );
    }

    private static Mono<String> userRepository(int userId) {
        return switch (userId) {
            case 1 -> Mono.just(RsUtil.faker().name().firstName());
            case 2 -> Mono.empty(); // returning Mono.empty() is a better way of sending an empty signal than null.
            default -> Mono.error(new RuntimeException("Not in the allowed range"));
        };
    }

    @Test
    public void mono_on_error_dropped_problem_test() {
        int userId = 3;

        // instead of using the default subscriber implementation, we are using a simple consumer implementation.
        // we will get onErrorDropped as the error handler is missing. To actually fix the issue, provide an error handler
        userRepository(userId)
                .subscribe(System.out::println, err -> log.info("Error message during subscription: {}", err.getMessage()));
    }
}
