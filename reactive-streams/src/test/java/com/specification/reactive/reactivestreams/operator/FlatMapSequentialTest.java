package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.User;
import com.specification.reactive.reactivestreams.service.OrderService;
import com.specification.reactive.reactivestreams.service.UserService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/* *
 * flatMapSequential(): This operator eagerly subscribes to its inner publishers just like flatMap(),
 * but queues up elements from later inner publishers to match the actual natural ordering and thereby prevents interleaving (events in order) like concatMap.
 * This operator can be used very similar to flatMap() or concatMap() bringing in both the benefits of flatMap() (i.e: speed or performance) and concatMap() (i.e: non-interleaving or ordering)
 *
 * In general, the operator is suitable for situations, where ordered non-interleaved values of concatMap with the performance advantage of flatMap is required
 * and the queue is not expected to grow infinitely.
 * */
@Slf4j
public class FlatMapSequentialTest {

    /* *
     * Get all orders from order service.
     * */
    @Test
    public void get_all_orders_from_order_service_using_flatMapSequential_test() {
        UserService.getAllUsers()
                .map(User::getUserId)
                .flatMapSequential(OrderService::getUserOrders)
                .subscribe(RsUtil.subscriber("order-subscriber"));

        RsUtil.sleepSeconds(2);
    }
}
