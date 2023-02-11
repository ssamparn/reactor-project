package com.specification.reactive.reactivestreams.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class FileReaderService {

    private static final Path PATH = Paths.get("src/test/resources/assignment");

    public static Mono<String> read(String fileName) {
        return Mono.fromSupplier(() -> readFile(fileName));
    }

    public static Mono<Void> write(String fileName, String content) {
        return Mono.fromRunnable(() -> writeFile(fileName, content));
    }

    public static Mono<Void> delete(String fileName) {
        return Mono.fromRunnable(() -> deleteFile(fileName));
    }

    private static String readFile(String fileName) {
        try {
            return Files.readString(PATH.resolve(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeFile(String fileName, String content) {
        try {
            Files.writeString(PATH.resolve(fileName), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteFile(String fileName) {
        try {
            Files.delete(PATH.resolve(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Flux<String> read(Path path) {
        return Flux.generate(
                openReader(path),
                readWithBufferedReader(),
                closeReader()
        );
    }

    private Callable<BufferedReader> openReader(Path path) {
        return () -> Files.newBufferedReader(path);
    }

    private BiFunction<BufferedReader, SynchronousSink<String>, BufferedReader> readWithBufferedReader() {
        return ((bufferedReader, stringSynchronousSink) -> {
            try {
                String line = bufferedReader.readLine();
                if (Objects.isNull(line)) {
                    stringSynchronousSink.complete();
                } else {
                    stringSynchronousSink.next(line);
                }
            } catch (IOException e) {
                stringSynchronousSink.error(e);
            }
            return bufferedReader;
        });
    }

    private Consumer<BufferedReader> closeReader() {
        return bufferedReader -> {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
