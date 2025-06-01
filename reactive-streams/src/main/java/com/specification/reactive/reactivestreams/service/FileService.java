package com.specification.reactive.reactivestreams.service;

import reactor.core.publisher.Mono;

/**
 * Used in Mono Assignment.
 * Assignment:
 *      Create File Service
 *          - Read file & return content
 *          - Create file & write content
 *          - Delete file
 * */

public interface FileService {

    Mono<String> read(String fileName);

    Mono<Void> write(String fileName, String content);

    Mono<Void> delete(String fileName);

}
