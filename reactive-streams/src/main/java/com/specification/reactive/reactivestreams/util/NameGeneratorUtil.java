package com.specification.reactive.reactivestreams.util;

import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class NameGeneratorUtil {

    public static List<String> getNamesViaList(int count) {
        List<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(getName());
        }
        return list;
    }

    public static Flux<String> getNamesViaFlux(int count) {
        return Flux.range(1, count)
                .map(i -> getName());
    }

    private static String getName() {
        ReactiveSpecificationUtil.sleepSeconds(1);
        return ReactiveSpecificationUtil.faker().name().fullName();
    }

}
