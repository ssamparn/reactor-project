package com.specification.reactive.reactivestreams.batching;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.util.List;

/* *
 * groupBy():
 * There is also one more way of grouping the elements based on some condition.
 * For example, We might want to collect based on some category! Splitting the flux into Odd and Even numbers flux.
 * In this case, we will have 2 different inner flux. Even number flux & Odd number flux.
 * So basically, groupBy() will be routing the events as and when it arrives to the corresponding flux based on the condition or any property.
 *
 * What is the advantage?
 * By grouping events like this by creating multiple fluxes, they all can be independently processed. So we can attach operators specific to those events.
 * For example, the even number might require some special handling, so we can attach additional operators specific to the even numbers.
 * Whereas, odd numbers might not require all those handling, so we do not have to attach those operators.
 *
 * V Imp Note: In case of window(), at any given point, there will be only one flux open. That is when you say window of duration of five seconds,
 * every five seconds it will open one inner flux. It will send all the items, it will close the that inner flux, then it will as part of the new window, will open one new flux.
 * So at any given point there will be only one flux that will be opened.
 *
 * But if you take groupBy() when you group by based on some condition, you will have multiple flux open & it will not be closed.
 * Because e.g, if you open a pink color flux, will you close it immediately? No, because there is a very good chance that after two hours the pink ball might come.
 * So in that case you will have to route it via this pink color flux, right?
 * So we will not be closing anything here because of that. That's why when you do groupBy(), you will have to ensure that you have the low cardinality.
 * That is, you should be having some reasonably sized flux like this. You should not be doing the groupBy based on the customer telephone number, because you will end up
 * having a million flux.
 * */
@Slf4j
public class GroupByOperatorTest {

    record RemainderCollector(int remainder, List<Integer> items) {

    }

    @Test
    public void group_by_operator_test() {
        Flux.range(1, 30)
                .groupBy(i -> i % 2) // 0 or 1. There would be 2 grouped flux. One with reminder 0 and one with reminder 1. We can access the reminder value by using key() of GroupedFlux.
                .flatMap(intGrFlux -> intGrFlux.collectList().map(list -> new RemainderCollector(intGrFlux.key(), list)))
        .subscribe(RsUtil.subscriber());
    }

    /* *
     * Be cautious when you use this groupBy().
     * The number of result-set should be low if possible and there should also be downstream consumers to drain the elements otherwise the inner flux will not be closed.
     * */

    @Test
    public void group_by_operator_processing_test() {
        Flux.range(1, 30)
                .groupBy(i -> i % 2)
                .flatMap(this::processGroupedFlux)
                .subscribe(RsUtil.subscriber());
    }

    private Mono<Void> processGroupedFlux(GroupedFlux<Integer, Integer> groupedFlux) {
        log.info("received flux for {}", groupedFlux.key());
        return groupedFlux
                .doOnNext(item -> log.info("key: {}, item: {}", groupedFlux.key(), item))
                .then();

        /* *
         * 2 things to note here:
         *  1. All the even numbers will have key 0 & all the odd numbers will have key 1.
         *  2. Each of the inner flux will be created only once.
         * */
    }
}