package com.specification.reactive.reactivestreams.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class User {
    private Integer userId;
    private String userName;
}
