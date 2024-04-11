package org.landsreyk.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.landsreyk.NumberRequest;
import org.landsreyk.NumberServiceGrpc;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NumbersClient {
    private static final int PORT = 8080;
    private static final int ITERATION_COUNT = 50;
    private static final ClientStreamObserver clientStreamObserver = new ClientStreamObserver();

    private static long value = 0;

    public static void main(String[] args) {
        log.info("numbers Client is starting...");
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", PORT)
                .usePlaintext()
                .build();
        NumberServiceGrpc.NumberServiceStub numberServiceStub = NumberServiceGrpc.newStub(managedChannel);
        perform(numberServiceStub);
        managedChannel.shutdown();
    }

    private static void perform(NumberServiceGrpc.NumberServiceStub numberServiceStub) {
        NumberRequest numberRequest = makeNumberRequest();
        numberServiceStub.number(numberRequest, clientStreamObserver);

        for (int i = 0; i < ITERATION_COUNT; i++) {
            long nextValue = getNextValue();
            log.info("currentValue:{}", nextValue);
            sleep(1, TimeUnit.SECONDS);
        }
    }

    /**
     * Получаем значение от сервера через стрим
     *
     * @return значение от сервера + текущее значение + 1
     */
    private static long getNextValue() {
        value = value + clientStreamObserver.getLastValueAndReset() + 1;
        return value;
    }

    /**
     * Формируем запрос gRPC
     *
     * @return сформированный объект запроса
     */
    private static NumberRequest makeNumberRequest() {
        return NumberRequest.newBuilder()
                .setFirstValue(0)
                .setLastValue(30)
                .build();
    }

    /**
     * Спим указанное количество единиц времени
     *
     * @param count    - количество единиц времени
     * @param timeUnit - единица измерения времени
     */
    private static void sleep(int count, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(count));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
