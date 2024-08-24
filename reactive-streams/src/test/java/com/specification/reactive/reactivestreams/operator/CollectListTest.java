package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.User;
import com.specification.reactive.reactivestreams.service.OrderService;
import com.specification.reactive.reactivestreams.service.UserService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* *
 * collectList(): To collect the events received from publisher Flux<T> in to a Mono<List<T>>, assuming the publisher Flux<T> has finite number of events.
 *                It's a non-blocking operation.
 *                If an error is emitted while collecting a Flux<T> to a Mono<List<T>>, then it will only emit error. Events will not be collected.
 * */
@Slf4j
public class CollectListTest {

    @Test
    public void collectList_test() {
        UserService.getAllUsers()
                .map(User::getUserId)
                .flatMap(OrderService::getUserOrders)
                .collectList()
                .subscribe(RsUtil.subscriber("order-subscriber"));

        RsUtil.sleepSeconds(2);
    }

    @Test
    public void collectList_simple_test() {
        Flux.range(1, 10)
                .collectList()
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(2);
    }

    @Test
    public void collectList_with_error_test() {
        Flux.range(1, 10)
                .concatWith(Mono.error(new RuntimeException("oops!")))
                .collectList()
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(2);
    }
}
