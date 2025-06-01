package com.specification.reactive.reactivestreams.service;

import lombok.extern.slf4j.Slf4j;
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

/* *
 * Used in Mono Assignment
 * */
@Slf4j
public class FileServiceImpl implements FileService {

    private static final Path PATH = Paths.get("src/test/resources/assignment");

    @Override
    public Mono<String> read(String fileName) {
        return Mono.fromCallable(() -> readFile(fileName)); // using Mono.fromCallable() as read file throws an IOException and a callable handles it.
    }

    @Override
    public Mono<Void> write(String fileName, String content) {
        return Mono.fromRunnable(() -> writeFile(fileName, content)); // using Mono.fromRunnable() as the return type is void, and we can return a publisher of type Mono<Void>.
    }

    @Override
    public Mono<Void> delete(String fileName) {
        return Mono.fromRunnable(() -> deleteFile(fileName)); // using Mono.fromRunnable() as the return type is void, and we can return a publisher of type Mono<Void>.
    }

    private static String readFile(String fileName) throws IOException {
        log.info("reading from file {}", fileName);
        return Files.readString(PATH.resolve(fileName));
    }

    private static void writeFile(String fileName, String content) {
        try {
            Files.writeString(PATH.resolve(fileName), content);
            log.info("created {}", fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteFile(String fileName) {
        try {
            Files.delete(PATH.resolve(fileName));
            log.info("deleted {}", fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* *
     *  Flux.generate(
     *      () -> someObjectInitialValue,   // invoked once
     *      (someObject, synchronousSink) -> {
     *          .....
     *          .....
     *          return someObject;
     *      }
     *      someObject -> close   // invoked once
     *  )
     */

    public Flux<String> readFileWithMultipleLines(Path path) {
        return Flux.generate(
                openFileReader(path),
                readWithBufferedReader(),
                closeReader()
        );
    }

    private Callable<BufferedReader> openFileReader(Path path) {
        log.info("Opening the File");
        return () -> Files.newBufferedReader(path);
    }

    private BiFunction<BufferedReader, SynchronousSink<String>, BufferedReader> readWithBufferedReader() {
        return ((bufferedReader, stringSynchronousSink) -> {
            try {
                String line = bufferedReader.readLine();
                log.info("reading line: {}", line);
                if (Objects.nonNull(line)) {
                    stringSynchronousSink.next(line);
                } else {
                    stringSynchronousSink.complete();
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
                log.info("Closing the File");
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
