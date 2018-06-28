package me.dong.retrybasic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RetryBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetryBasicApplication.class, args);
    }
}

// TODO: 2018. 6. 27. 다른 module에서  https://dzone.com/articles/asynchronous-retry-pattern 해보기