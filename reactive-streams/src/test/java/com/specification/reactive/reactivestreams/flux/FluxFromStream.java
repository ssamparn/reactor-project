package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class FluxFromStream {

    @Test
    public void flux_from_stream_test() {
        Stream<String> nameStream = Stream.of("Sam", "Harry", "Bapun", "Sashank");

        Flux.fromStream(nameStream).subscribe(
                name -> log.info("Stream Subscriber : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );

        Flux.fromStream(nameStream).subscribe(
                name -> log.info("Stream Subscriber : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }

    @Test
    public void flux_from_stream_supplier_test() {
        Stream<String> nameStream = Stream.of("Sam", "Harry", "Bapun", "Sashank");

        Flux.fromStream(() -> nameStream).subscribe(
                name -> log.info("Stream Subscriber : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );

        Flux.fromStream(() -> nameStream).subscribe(
                name -> log.info("Stream Subscriber : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }

    @Test
    public void flux_from_stream_supplier_fromList_test() {
        List<String> nameList = List.of("Sam", "Harry", "Bapun", "Sashank");

        Flux.fromStream(nameList::stream).subscribe(
                name -> log.info("Stream Subscriber : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );

        Flux.fromStream(nameList::stream).subscribe(
                name -> log.info("Stream Subscriber : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }
}
