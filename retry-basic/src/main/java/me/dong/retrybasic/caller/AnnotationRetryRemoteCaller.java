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

    /**
     * stateless retry
     * <p>
     * - MyException에 대해 최대 3번 시도
     * - NullPointerException은 재시도하지 않는다
     * - backoff 간격은 2000ms ~ 5000ms에서 랜덤으로 결정
     *
     * @param url
     * @return
     */
    @Retryable(include = MyException.class, exclude = NullPointerException.class,
            maxAttempts = 3, backoff = @Backoff(delay = 2000, maxDelay = 5000), stateful = false,
            exceptionExpression = "#{message.contains('this can be retried')}}")
    @Override
    public String call(String url) {
        log.info("call : {}", url);

        return restTemplate.getForEntity(url, String.class).getBody();
    }

    /**
     * stateful retry
     * <p>
     * - MyException에 대해 최대 4번 시도
     * - backoff 간격은 2000ms ~ 5000ms에서 랜덤으로 결정
     * - state 저장을 위해 key로 사용할 method param 필요
     * - exceptionExpression -> throw 된 exception에 대해 #root 객체로 평가
     * - maxAttemptsExpression과 @BackOff 속성
     * -- 초기화시 1번만 평가
     * -- 평가를 위한 root 객체는 없지만 context에서 다른 bean 참조 가능
     *
     * @param url
     * @return
     */
    @Retryable(include = MyException.class, stateful = true, maxAttempts = 4,
            backoff = @Backoff(delay = 2000, maxDelay = 5000, delayExpression = "#{1}", maxDelayExpression = "#{5}", multiplierExpression = "#{1.1}"),
            exceptionExpression = "#{@exceptionChecker.shouldRetry(#root)}}", maxAttemptsExpression = "#{@integerFiveBean}}")
    public String statefulCall(String url) {
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