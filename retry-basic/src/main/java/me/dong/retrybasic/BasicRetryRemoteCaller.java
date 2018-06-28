package me.dong.retrybasic;

import org.springframework.retry.annotation.Recover;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by ethan.kim on 2018. 5. 24..
 */
@Component("basicRetryRemoteCaller")
@Slf4j
public class BasicRetryRemoteCaller implements RemoteCaller {

    private final RetryTemplate retryTemplate;

    private final RestTemplate restTemplate;

    public BasicRetryRemoteCaller(RetryTemplate retryTemplate, RestTemplate restTemplate) {
        this.retryTemplate = retryTemplate;
        this.restTemplate = restTemplate;
    }

    @Override
    public String call(String url) {
        return retryTemplate.execute(context -> {
            log.info("call : {}", url);

            return restTemplate.getForEntity(url, String.class).getBody();
        });
    }

    @Recover
    public void recover(Exception e) {
        log.info("recover : {}", e.getMessage());
    }
}


