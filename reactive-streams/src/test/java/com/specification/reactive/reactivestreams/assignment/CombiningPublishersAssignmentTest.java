package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.model.Order;
import com.specification.reactive.reactivestreams.model.User;
import com.specification.reactive.reactivestreams.service.OrderService;
import com.specification.reactive.reactivestreams.service.PaymentService;
import com.specification.reactive.reactivestreams.service.UserService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

/* *
 * Get all users, username, user balance & all orders made by the user. Build one object as below.
 * record UserInformation(Integer userId, String username, Integer balance, List<Order> orders) { }
 * */
@Slf4j
public class CombiningPublishersAssignmentTest {

    record UserInformation(Integer userId, String username, Integer balance, List<Order> orders) {

    }

    @Test
    public void combining_publishers_assignment_test() {
        UserService.getAllUsers()
                .flatMap(CombiningPublishersAssignmentTest::getUserInformation)
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(2);
    }

    private static Mono<UserInformation> getUserInformation(User user) {
        return Mono.zip(PaymentService.getUserBalance(user.getUserId()), OrderService.getUserOrders(user.getUserId()).collectList())
                .map(tuple -> new UserInformation(user.getUserId(), user.getUserName(), tuple.getT1(), tuple.getT2()));
    }
}
