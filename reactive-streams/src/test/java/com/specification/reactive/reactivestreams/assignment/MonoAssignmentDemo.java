package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.service.FileServiceImpl;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;

public class MonoAssignmentDemo {

    private FileServiceImpl fileService = new FileServiceImpl();
    @Test
    public void mono_assignment() {
        fileService.read("file01.txt")
                .subscribe(
                        RsUtil.onNext(),
                        RsUtil.onError(),
                        RsUtil.onComplete()
                );

        fileService.write("file03.txt", "I am going into file03")
                .subscribe(
                        RsUtil.onNext(),
                        RsUtil.onError(),
                        RsUtil.onComplete()
                );

        fileService.delete("file03.txt")
                .subscribe(
                        RsUtil.onNext(),
                        RsUtil.onError(),
                        RsUtil.onComplete()
                );
    }
}
