package com.specification.reactive.flux;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

public class FluxFromArrayAndList {

    @Test
    public void fluxFromListTest() {
        List<String> nameList = Arrays.asList("Sam", "Harry", "Bapun", "Sashank");

        Flux.fromIterable(nameList).subscribe(
                    name -> System.out.println("List Subscriber : " + name),
                    ReactiveSpecificationUtil.onError(),
                    ReactiveSpecificationUtil.onComplete()
        );
    }

    @Test
    public void fluxFromArrayTest() {
        String[] nameArray = {"Sam", "Harry", "Bapun", "Sashank"};

        Flux.fromArray(nameArray).subscribe(
                name -> System.out.println("Array Subscriber : " + name),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }
}
