package com.specification.reactive.reactivestreams.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestUtil {

    public static List<String> convertToList(String element) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(element, "newValue");
    }

    public static List<String> splitStringWithDelay(String element) {
        String[] strings = element.split("");
        return Arrays.asList(strings);
    }
}
