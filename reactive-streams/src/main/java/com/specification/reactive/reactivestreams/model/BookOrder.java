package com.specification.reactive.reactivestreams.model;

import com.specification.reactive.reactivestreams.util.RsUtil;

public record BookOrder(String genre,
                        String title,
                        Integer price) {

    public static BookOrder create() {
        return new BookOrder(
                RsUtil.faker().book().genre(),
                RsUtil.faker().book().title(),
                RsUtil.faker().number().numberBetween(100, 200));
    }
}