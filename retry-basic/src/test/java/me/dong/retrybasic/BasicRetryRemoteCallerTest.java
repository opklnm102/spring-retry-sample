package me.dong.retrybasic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 5. 24..
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicRetryRemoteCallerTest {

    private RemoteCaller sut;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(4);

        sut = new BasicRetryRemoteCaller(retryTemplate, restTemplate);
    }

    @Test
    public void call() {
        // given :
        String url = "test";

        when(restTemplate.getForEntity(url, String.class))
                .thenThrow(new RuntimeException())
                .thenReturn(ResponseEntity.ok("OK"));

        // when :
        String message = sut.call(url);

        // then :
        verify(restTemplate, times(2)).getForEntity(url, String.class);
        assertThat(message).isEqualTo("OK");
    }
}
