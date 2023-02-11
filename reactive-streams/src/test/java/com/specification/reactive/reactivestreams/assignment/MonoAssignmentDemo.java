package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.service.FileReaderService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;

public class MonoAssignmentDemo {

    @Test
    public void mono_assignment() {
        FileReaderService.read("file01.txt")
                .subscribe(
                        RsUtil.onNext(),
                        RsUtil.onError(),
                        RsUtil.onComplete()
                );

        FileReaderService.write("file03.txt", "This is file03")
                .subscribe(
                        RsUtil.onNext(),
                        RsUtil.onError(),
                        RsUtil.onComplete()
                );

        FileReaderService.delete("file03.txt")
                .subscribe(
                        RsUtil.onNext(),
                        RsUtil.onError(),
                        RsUtil.onComplete()
                );
    }
}
