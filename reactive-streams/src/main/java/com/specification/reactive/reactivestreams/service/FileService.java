package com.specification.reactive.reactivestreams.service;

import reactor.core.publisher.Mono;

public interface FileService {

    Mono<String> read(String fileName);

    Mono<Void> write(String fileName, String content);

    Mono<Void> delete(String fileName);

}
