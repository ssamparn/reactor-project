package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.User;
import com.specification.reactive.reactivestreams.service.OrderService;
import com.specification.reactive.reactivestreams.service.UserService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ConcatMapOperatorTest {

    /* *
     * concatMap(): The concatMap operator is actually quite similar to flatMap(), except that the operator waits for its inner publishers to complete before subscribing to the next one.
     * In comparison to flatMap() one must admit that concatMap() is potentially less performant as it has to utilize resources to maintain the ordering & it subscribes to its inner publishers lazily one after another.
     * With flatMap() the total runtime mainly depends on the slowest publisher because of its eager subscription to inner publishers.
     * Whereas concatMap() waits for its inner publisher to finish before continuing with the next item (lazy subscription).
     * As a compromise between flatMap() and concatMap(), the operator flatMapSequential() exists.
     *
     * Note: As we saw behavior of flatMap() is same as the behavior of merge() or mergeWith().
     * Similarly, behavior of concatMap() is same as the behavior of concat() or concatWith().
     * */

    /* *
     * Get all orders from order service.
     * */
    @Test
    public void get_all_orders_from_order_service_using_concatMap_test() {
        UserService.getAllUsers()
                .map(User::getUserId)
                .concatMap(OrderService::getUserOrders)
                .subscribe(RsUtil.subscriber("order-subscriber"));

        RsUtil.sleepSeconds(2);
    }
}
