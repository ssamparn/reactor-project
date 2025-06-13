package com.nonblocking.http.client.assignment.context;

import reactor.util.context.Context;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * To be used in reactive context demo
 * */
public class UserService {

    private static final Map<String, String> USER_CATEGORY = Map.of(
            "sam", "standard",
            "mike", "prime"
    );

    public static UnaryOperator<Context> userCategoryContext() {
        return ctx -> ctx.<String>getOrEmpty("user") // gets the "user" key's value (sam or mike or something else) set in the context before .
                .filter(USER_CATEGORY::containsKey) // filters the "user" key's value (sam or mike or something else
                .map(USER_CATEGORY::get) // gets the value of key. category is the value (standard or prime)
                .map(category -> ctx.put("category", category)) // sets the category value (standard or prime)
                .orElse(Context.empty()); // or else returning empty context.
    }

}
