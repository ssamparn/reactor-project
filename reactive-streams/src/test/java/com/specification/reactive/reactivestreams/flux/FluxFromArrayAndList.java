package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class FluxFromArrayAndList {

    @Test
    public void flux_from_list_test() {
        List<String> nameList = Arrays.asList("Sam", "Harry", "Bapun", "Sashank");

        Flux.fromIterable(nameList).subscribe(
                    name -> log.info("List Subscriber : {}", name),
                    RsUtil.onError(),
                    RsUtil.onComplete()
        );
    }

    @Test
    public void flux_from_array_test() {
        String[] nameArray = {"Sam", "Harry", "Bapun", "Sashank"};

        Flux.fromArray(nameArray).subscribe(
                name -> log.info("Array Subscriber : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }
}
