package me.dong.retrybasic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 5. 24..
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AnnotationRetryRemoteCallerTest {

    @Qualifier("annotationRetryRemoteCaller")
    @Autowired
    private RemoteCaller sut;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void call() throws Exception {
        // given :
        String url = "test";

        when(restTemplate.getForEntity(url, String.class))
                .thenThrow(new MyException())
                .thenReturn(ResponseEntity.ok("OK"));

        // when :
        String message = sut.call(url);

        // then :
        verify(restTemplate, times(2)).getForEntity(url, String.class);
        assertThat(message).isEqualTo("OK");
    }
}