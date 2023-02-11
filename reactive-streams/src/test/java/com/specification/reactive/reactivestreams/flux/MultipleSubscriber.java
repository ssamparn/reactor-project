package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class MultipleSubscriber {

    @Test
    public void multiple_subscriber_test() {
        Flux<String> nameFlux = Flux.just("Sam", "Harry", "Bapun", "Sashank");

        Flux<String> sNameFlux = nameFlux.filter(name -> name.startsWith("S"));

        nameFlux.subscribe(
                name -> System.out.println("All names: Subscriber: 1 : " + name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );

        sNameFlux.subscribe(
                name -> System.out.println("Names starts with S: Subscriber: 2 : " + name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }
}
