package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.model.Order;
import com.specification.reactive.reactivestreams.service.OrderProcessingService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

/* *
 *  We have a stream of Orders.
 *  record Order(String productName, String category, Integer price) {
 *
 *  }
 * If category = "Kids" => Add 1 free order for every kids.
 * If category = "Automotive" => add $100 to the price
 * */
@Slf4j
public class GroupByAssignmentTest {

    @Test
    public void group_assignment_test() {
        Flux.interval(Duration.ofMillis(5))
                .map(i -> new Order())
                .filter(OrderProcessingService.canProcessOrder())
                .groupBy(Order::getCategory)
                .flatMap(gf -> gf.transform(OrderProcessingService.getProcessor(gf.key())))
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(5);
    }
}