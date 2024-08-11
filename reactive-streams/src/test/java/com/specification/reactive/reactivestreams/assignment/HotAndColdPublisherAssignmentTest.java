package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.service.InventoryService;
import com.specification.reactive.reactivestreams.service.OrderService;
import com.specification.reactive.reactivestreams.service.RevenueService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class HotAndColdPublisherAssignmentTest {

    @Test
    public void hotAndColdPublisherAssignment() {
        OrderService orderService = new OrderService();
        RevenueService revenueService = new RevenueService();
        InventoryService inventoryService = new InventoryService();

        // subscribe to revenue and inventory service
        orderService.getOrderStream().subscribe(revenueService.subscribeOrderStream());
        orderService.getOrderStream().subscribe(inventoryService.subscribeOrderStream());

        inventoryService.inventoryStream()
                .subscribe(RsUtil.subscriber("inventory"));
        revenueService.revenueStream()
                .subscribe(RsUtil.subscriber("revenue"));

        RsUtil.sleepSeconds(10);

    }
}
