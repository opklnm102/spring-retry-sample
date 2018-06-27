package me.dong.retrybasic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by ethan.kim on 2018. 5. 24..
 */
@Service
@EnableScheduling
public class OrderService {

    private final RemoteCaller annotationRetryRemoteCaller;

    private final RemoteCaller basicRetryRemoteCaller;

    public OrderService(@Qualifier("annotationRetryRemoteCaller") RemoteCaller remoteCaller,
                        @Qualifier("basicRetryRemoteCaller") RemoteCaller basicRetryRemoteCaller) {
        this.annotationRetryRemoteCaller = remoteCaller;
        this.basicRetryRemoteCaller = basicRetryRemoteCaller;
    }

    @Scheduled(fixedDelay = 10000)
    public void createOrder() {
        annotationRetryRemoteCaller.call("testUrl");

        basicRetryRemoteCaller.call("testUrl2");
    }
}
