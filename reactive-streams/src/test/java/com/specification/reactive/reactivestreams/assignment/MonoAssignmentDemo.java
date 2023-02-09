package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.service.FileService;
import com.specification.reactive.reactivestreams.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;

public class MonoAssignmentDemo {

    @Test
    public void monoAssignment() {
        FileService.read("file01.txt")
                .subscribe(
                        ReactiveSpecificationUtil.onNext(),
                        ReactiveSpecificationUtil.onError(),
                        ReactiveSpecificationUtil.onComplete()
                );

        FileService.write("file03.txt", "This is file03")
                .subscribe(
                        ReactiveSpecificationUtil.onNext(),
                        ReactiveSpecificationUtil.onError(),
                        ReactiveSpecificationUtil.onComplete()
                );

        FileService.delete("file03.txt")
                .subscribe(
                        ReactiveSpecificationUtil.onNext(),
                        ReactiveSpecificationUtil.onError(),
                        ReactiveSpecificationUtil.onComplete()
                );
    }
}
