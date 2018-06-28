package me.dong.retrybasic;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import me.dong.retrybasic.common.aspect.Retry;

/**
 * Created by ethan.kim on 2018. 6. 28..
 */
@Component("aspectRetryRemoteCaller")
@Slf4j
public class AspectRetryRemoteCaller implements RemoteCaller {

    private final RestTemplate restTemplate;

    public AspectRetryRemoteCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retry
    @Override
    public String call(String url) {
        log.info("call : {}", url);

        return restTemplate.getForEntity(url, String.class).getBody();
    }
}
