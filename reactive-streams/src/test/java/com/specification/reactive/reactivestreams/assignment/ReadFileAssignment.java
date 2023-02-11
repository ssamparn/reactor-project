package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.service.FileReaderService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadFileAssignment {

    @Test
    public void read_file_assignment() {
        FileReaderService fileReaderService = new FileReaderService();
        Path path = Paths.get("src/test/resources/assignment/file03.txt");

        Flux<String> lineFlux = fileReaderService.read(path);

        lineFlux
            .take(10)
            .subscribe(RsUtil.subscriber());
    }

}
