package com.specification.reactive.reactivestreams.model;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {
    private int userId;
    private String userName;

    public User(int userId) {
        this.userId = userId;
        this.userName = RsUtil.faker().name().fullName();
    }
}
