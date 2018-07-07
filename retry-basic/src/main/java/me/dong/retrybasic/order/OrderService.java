package me.dong.retrybasic.order;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import me.dong.retrybasic.caller.RemoteCaller;

/**
 * Created by ethan.kim on 2018. 5. 24..
 */
@Service
@EnableScheduling
@Profile("!test")
public class OrderService {

    private final RemoteCaller annotationRetryRemoteCaller;

    private final RemoteCaller basicRetryRemoteCaller;

    private final RemoteCaller aspectRetryRemoteCaller;

    public OrderService(@Qualifier("annotationRetryRemoteCaller") RemoteCaller remoteCaller,
                        @Qualifier("basicRetryRemoteCaller") RemoteCaller basicRetryRemoteCaller,
                        @Qualifier("aspectRetryRemoteCaller") RemoteCaller aspectRetryRemoteCaller) {
        this.annotationRetryRemoteCaller = remoteCaller;
        this.basicRetryRemoteCaller = basicRetryRemoteCaller;
        this.aspectRetryRemoteCaller = aspectRetryRemoteCaller;
    }

    @Scheduled(fixedDelay = 10000)
    public void createOrder() {
        annotationRetryRemoteCaller.call("annotation retry testUrl");

        aspectRetryRemoteCaller.call("aspect retry testUrl");

        basicRetryRemoteCaller.call("basic retry testUrl");
    }
}
