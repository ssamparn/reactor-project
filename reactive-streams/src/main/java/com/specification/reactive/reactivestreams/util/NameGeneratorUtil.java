package com.specification.reactive.reactivestreams.util;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.IntStream;

public class NameGeneratorUtil {

    public static List<String> getNamesList(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> getName())
                .toList();
    }

    public static Flux<String> getNamesFlux(int count) {
        return Flux.range(1, count)
                .map(i -> getName());
    }

    private static String getName() {
        RsUtil.sleepSeconds(1);
        return RsUtil.faker().name().fullName();
    }

}
