package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.User;
import com.specification.reactive.reactivestreams.service.PurchaseOrderService;
import com.specification.reactive.reactivestreams.service.UserService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;

public class FlatMapOperatorTest {

    @Test
    public void flatMap_operator_test() {
        UserService.getUsers()
                .map(User::getUserId)
                .flatMap(PurchaseOrderService::getOrders)
                .subscribe(RsUtil.subscriber());

        // Shortcut: If the return type is a publisher (flux or mono), then 99.99% of the time you have to use flatMap to flatten it.
    }
}
