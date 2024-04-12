package org.landsreyk.grpc.client;

import lombok.extern.slf4j.Slf4j;
import org.landsreyk.NumberResponse;

@Slf4j
public class ClientStreamObserver implements io.grpc.stub.StreamObserver<org.landsreyk.NumberResponse> {

    private long lastValue = 0;

    @Override
    public void onNext(NumberResponse numberResponse) {
        setLastValue(numberResponse.getNumber());
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("error: ", throwable);
    }

    @Override
    public void onCompleted() {
        log.info("request completed");
    }

    private synchronized void setLastValue(long value) {
        log.info("new value:{}", value);
        this.lastValue = value;
    }

    /**
     * Возвращает последнее значение и обнуляет переменную, чтобы старое значение не использовалось в дальнейших рассчетах
     *
     * @return последнее полученное значение счетчика
     */
    public synchronized long getLastValueAndReset() {
        long lastValuePrev = this.lastValue;
        this.lastValue = 0;
        return lastValuePrev;
    }
}
