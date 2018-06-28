package me.dong.retrybasic.caller;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import me.dong.retrybasic.common.exception.MyException;

/**
 * Created by ethan.kim on 2018. 5. 24..
 */
@Component("annotationRetryRemoteCaller")
@Slf4j
public class AnnotationRetryRemoteCaller implements RemoteCaller {

    private final RestTemplate restTemplate;

    public AnnotationRetryRemoteCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(include = MyException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, maxDelay = 5000))
    @Override
    public String call(String url) {
        log.info("call : {}", url);

        return restTemplate.getForEntity(url, String.class).getBody();
    }

    // @Retryable method와 return type이 같으면 실행된다
    // private여도 실행
    @Recover
    private String recover() {
        log.info("recover");
        return "";
    }

    // 여러개의 @Recover가 있을 경우 더 자세한(parameter가 있는) 메소드가 실행된다
    // 이게 실행된다
    @Recover
    public String recover(MyException e, String url) {
        log.info("recover : {}, url : {}", e.getClass(), url);
        return url;
    }

    // 실행이 안된다
    @Recover
    public void recoverVoid(MyException e, String url) {
        log.info("recover : {}, url : {}", e.getClass(), url);
    }
}