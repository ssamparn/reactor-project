package com.specification.reactive.reactivestreams.combine.publishers.startwith;

import com.specification.reactive.reactivestreams.service.NameProducer;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;

public class CombinePublisherStartWithTest {

    @Test
    public void combinePublisherStartWithTest() {

        NameProducer nameProducer = new NameProducer();
        
        // Subscriber - 1
        nameProducer
                .generateNames()
                .take(3)
                .subscribe(RsUtil.subscriber("Sashank"));

        // Subscriber - 2
        nameProducer
                .generateNames()
                .take(4) // Here the first 3 items will be received from the cache
                // and rest 1 item will be generated fresh
                .subscribe(RsUtil.subscriber("Aparna"));

        // Subscriber - 3
        nameProducer
                .generateNames()
                .take(9) // Here the first 4 items will be received from the cache
                // and rest 5 item will be generated fresh
                .subscribe(RsUtil.subscriber("Monalisa"));


        // Subscriber - 4
        nameProducer
                .generateNames()
                .filter(name -> name.startsWith("B")) // If any of the previously emitted item starts with Z, then it will be retrieved from the cache.
                .take(1)
                .subscribe(RsUtil.subscriber("Monalisa"));
    }
}
