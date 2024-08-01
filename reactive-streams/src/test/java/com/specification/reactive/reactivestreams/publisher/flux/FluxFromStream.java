package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class FluxFromStream {

    @Test
    public void flux_from_stream_test() {
        List<String> stringList = Arrays.asList("Sam", "Harry", "Bapun", "Sashank");
        Stream<String> nameStream = stringList.stream();

        Flux.fromStream(nameStream)
                .subscribe(
                    name -> log.info("Stream Subscriber : {}", name),
                    RsUtil.onError(),
                    RsUtil.onComplete()
        );

        // This will result in an error as a stream can only be processed once.
        // Error: Stream has already been operated upon or closed. So you should be using a supplier of stream like the below example.

        Flux.fromStream(nameStream)
                .subscribe(
                    name -> log.info("Stream Subscriber : {}", name),
                    RsUtil.onError(),
                    RsUtil.onComplete()
        );
    }

    @Test
    public void flux_from_stream_supplier_test() {
        List<String> stringList = Arrays.asList("Sam", "Harry", "Bapun", "Sashank");
        Stream<String> nameStream = stringList.stream();

        Flux.fromStream(() -> nameStream).subscribe(
                name -> log.info("Stream Subscriber : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );

        // This will result in an error as a stream can only be processed once.
        // Error: Stream has already been operated upon or closed. So you should be using a supplier of stream like the below example.
        Flux.fromStream(() -> nameStream).subscribe(
                name -> log.info("Stream Subscriber : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }

    @Test
    public void flux_from_stream_supplier_fromList_test() {
        List<String> nameList = List.of("Sam", "Harry", "Bapun", "Sashank");

        Flux.fromStream(nameList::stream)
                .subscribe(
                    name -> log.info("Stream Subscriber : {}", name),
                    RsUtil.onError(),
                    RsUtil.onComplete()
        );

        Flux.fromStream(nameList::stream)
                .subscribe(
                    name -> log.info("Stream Subscriber : {}", name),
                    RsUtil.onError(),
                    RsUtil.onComplete()
        );
    }
}
