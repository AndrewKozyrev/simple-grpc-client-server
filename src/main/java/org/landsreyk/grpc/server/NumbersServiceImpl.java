package org.landsreyk.grpc.server;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.landsreyk.NumberRequest;
import org.landsreyk.NumberResponse;
import org.landsreyk.NumberServiceGrpc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class NumbersServiceImpl extends NumberServiceGrpc.NumberServiceImplBase {

    @Override
    public void number(NumberRequest request, StreamObserver<NumberResponse> responseObserver) {
        log.info("request for the new sequence of numbers, firstValue:{}, lastValue:{}", request.getFirstValue(), request.getLastValue());
        AtomicLong currentValue = new AtomicLong(request.getFirstValue());

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            long value = currentValue.incrementAndGet();
            NumberResponse response = NumberResponse.newBuilder().setNumber(value).build();
            responseObserver.onNext(response);
            if (value == request.getLastValue()) {
                executor.shutdown();
                responseObserver.onCompleted();
                log.info("sequence of numbers finished");
            }
        };
        executor.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
    }
}
