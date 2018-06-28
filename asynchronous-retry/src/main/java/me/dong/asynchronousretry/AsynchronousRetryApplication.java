package me.dong.asynchronousretry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * https://dzone.com/articles/asynchronous-retry-pattern
 */
@SpringBootApplication
public class AsynchronousRetryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsynchronousRetryApplication.class, args);
    }
}
