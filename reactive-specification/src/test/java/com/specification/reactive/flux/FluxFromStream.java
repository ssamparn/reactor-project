package com.specification.reactive.flux;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

public class FluxFromStream {

    @Test
    public void fluxFromStreamTest() {
        Stream<String> nameStream = Stream.of("Sam", "Harry", "Bapun", "Sashank");

        Flux.fromStream(nameStream).subscribe(
                name -> System.out.println("Stream Subscriber : " + name),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );

        Flux.fromStream(nameStream).subscribe(
                name -> System.out.println("Stream Subscriber : " + name),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }

    @Test
    public void fluxFromStreamSupplierTest() {
        Stream<String> nameStream = Stream.of("Sam", "Harry", "Bapun", "Sashank");

        Flux.fromStream(() -> nameStream).subscribe(
                name -> System.out.println("Stream Subscriber : " + name),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );

        Flux.fromStream(() -> nameStream).subscribe(
                name -> System.out.println("Stream Subscriber : " + name),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }

    @Test
    public void fluxFromStreamSupplierFromListTest() {
        List<String> nameList = List.of("Sam", "Harry", "Bapun", "Sashank");

        Flux.fromStream(nameList::stream).subscribe(
                name -> System.out.println("Stream Subscriber : " + name),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );

        Flux.fromStream(nameList::stream).subscribe(
                name -> System.out.println("Stream Subscriber : " + name),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }
}
