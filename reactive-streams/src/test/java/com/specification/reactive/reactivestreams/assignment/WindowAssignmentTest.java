package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/* *
 * Fraud Detection:
 * Let’s consider streams of credit card transactions.
 * As per our business rule, If a credit card is used 3 times in the last 2 seconds, then something is wrong!
 * It could be a fraudulent activity and the card should be blocked!
 * Even though this requirement sounds very simple, it would be very challenging to implement without windowing!
 * */
@Slf4j
public class WindowAssignmentTest {

    @Test
    public void fraud_detection_test() {
        getCreditCardTransactions()
                .doOnNext(this::fraudDetector)
                .subscribe();

        RsUtil.sleepSeconds(5);
    }

    private void fraudDetector(Flux<Integer> creditCardFlux) {
        creditCardFlux
                .collectList() // Mono<List<Integer>>
                .map(cards -> cards.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))) // Grouping cards by their usage count. returns a Mono<Map<Integer, Long>>. Key of the map is the card itself, and the value is the usage count.
                .doOnNext(cardMap -> cardMap.entrySet().removeIf(entry -> entry.getValue() < 3)) // removing cards from the map if the usage is less than 3.
                .filter(cardMap -> !cardMap.isEmpty()) // additional empty check
                .map(Map::keySet) // all the fraud cards
                .subscribe(card -> log.info("Fraud Card: {}", card));
    }

    public Flux<Flux<Integer>> getCreditCardTransactions() {
        Flux<Integer> creditCard1 = Flux.just(100, 300, 302, 200, 100, 104, 200)
                .delayElements(Duration.ofMillis(400));

        Flux<Integer> creditCard2 = Flux.just(100, 101, 102, 100, 105, 102, 104)
                .delayElements(Duration.ofMillis(500));

        Flux<Integer> creditCard3 = Flux.just(101, 200, 201, 300, 102, 301, 100)
                .delayElements(Duration.ofMillis(600));

        return Flux.merge(creditCard1, creditCard2, creditCard3)
                .window(Duration.ofSeconds(2), Duration.ofMillis(500)); // create a flux of 2 seconds every 500 milliseconds
    }
}