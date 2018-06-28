package me.dong.retrybasic.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by ethan.kim on 2018. 6. 28..
 */
@Aspect
@Component
@Slf4j
public class RetryAspect {

    private final RetryTemplate retryTemplate;

    public RetryAspect(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    @Pointcut(value = "@annotation(me.dong.retrybasic.common.aspect.Retry)" +
            "&& execution(* me.dong.retrybasic..*(..))")
    public void retryTargetMethod() {
    }

    @Around("retryTargetMethod()")
    public Object aroundCaller(ProceedingJoinPoint pjp) {
        try {
            return retryTemplate.execute(context -> pjp.proceed());
        } catch (Throwable e) {
            log.error("retry error. message : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
