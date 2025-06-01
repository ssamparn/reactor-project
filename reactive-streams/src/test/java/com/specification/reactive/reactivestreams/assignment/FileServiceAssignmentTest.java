package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.service.FileServiceImpl;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Used in Flux.generate() section assignment
 * */
public class FileServiceAssignmentTest {

    @Test
    public void read_file_assignment() {
        FileServiceImpl fileServiceImpl = new FileServiceImpl();
        Path path = Paths.get("src/test/resources/assignment/file03.txt");

        Flux<String> lineFlux = fileServiceImpl.readFileWithMultipleLines(path);

        lineFlux
            .subscribe(RsUtil.subscriber("File Reader Subscriber"));
    }

}
