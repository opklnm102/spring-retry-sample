package me.dong.asynchronousretry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * https://dzone.com/articles/asynchronous-retry-pattern
 * https://github.com/nurkiewicz/async-retry
 */
@SpringBootApplication
public class AsynchronousRetryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsynchronousRetryApplication.class, args);
    }

    public void test () throws Exception {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


        // async로 동작할 때 therad pool에 있는 thread가 모두 사용되는지 check

        /*
        CompletableFuture를 즉시 반환하고 지정된 함수를 비동기적으로 호출

        future, multiple future를 listen하는 동안 다른 일을 할 수 있다




        localhost:8080 에 연결하려고 시도하고 SocketException으로 실패시 500ms 후 재시도
        재시도 후 2배의 지연되지만 10초를 초과하지는 않는다

        executor.getWithRetry(() -> new Socket("localhost", 8080))
                .thenAccept(socket -> print("Connected! " + socket));

        2개의 서로 다른 시스템에 연결한다고 상상
        하나는 느리고, 2번째는 신뢰할 수 없으며 자주 실패

        CompletableFuture<String> stringFuture = executor.getWithRetry(ctx -> unreliable());
        CompletableFuture<Integer> intFuture = executor.getWithRetry(ctx -> slow());

        stringFuture.thenAcceptBoth(intFuture, (String s, Integer i) -> {
            // both done after some retries
        });

        느리고 신뢰할 수 없는 시스템이 결국 실패없이 응답할 때 thenAcceptBoth() callback이 비동기적으로 실행된다

        마찬가지로 CompletableFuture.acceptEither()로 신뢰할 수 없는 2개 이상의 서버를 동시에 비동기적으로 호출하고
        몇번 재시도 횟수가 지나면 1번째 서버가 성공할 때 알림을 받을 수 있다

        이점을 강조할 수 없다
          재시도는 비동기적으로 실행되고 block되지 않고, thread pool을 효과적으로 사용한다



        # 이론적 해석
        작업이 실패했기 때문에 재시도해야 하는 경우가 종종 있다
        리소스를 낭비하지 않기 위해 약간의 지연이 필요

        이 요구사항은 매우 일반적이며

        그러나 다른 비슷한 접근법은 거의 없다

        기존의 문제는.. blocking하고 있어 많은 자원을 낭비하고 잘 확장하지 못한다

        프로그래밍 모델을 훨씬 단순하게 만들기 때문에 이것은 나쁘지 않다
        리이브러리가 재시도를 처리하고 평소보다 반환값을 기다리지 않으면됩니다

        그러나 재시도 및 지연으로 인해 매우 빠른 메소드가 갑자기 느려지고,
        thread 재사용과 재시작 사이에 대부분의 시간을 소비하기 때문에 비싼 thread가 낭비된다

        그래서 Async-Retry가 만들어졌다


        메인 추상화는 RetryExecutor

        CompletableFuture 를 반환하는 점을 유의
        우리는 더이상 불안정한 메소드가 빠르다고 가장하지 않는다

        라이브러리에 예외가 발생하면 사전에 구성된 backoff delay를 사용하여 재시도
        호출 시간은 ms ~ s까지 빨라질 것
        CompletableFuture는 그것을 분명하게 나타낸다

        그것은 Future가 아니다
        CompletableFuture는 Java8에서 매우 강력하고 중요한 기능
        기본적으로 non-blocking

        blocking 결과가 필요하다면 Future.get()을 호출



        # Basic API
        API는 매우 간단하다

        코드 block을 제공하면 라이브러리는 exception을 던지기 보다는 정상적으로 반환할 떄 까지 여러번 실행
        또한 재시도 간에 구성가능한 지연을 둘 수 있다

        RetryExecutor executor = // ...
        executor.getWithRetry(() -> new Socket("localhost", 8080));

        반환된 CompletableFuture<Socket>은 localhost에 연결되면 해결된다

        선택적으로 RetryContext를 소비하여 재시도가 현재 실행중인 것과 같은 추가 context를 얻을 수 있다
        executor.getWithRetry(ctx -> new Socket("localhost", 8008 + ctx.getRetryCount()))
                .thenAccept(System.out::println);

        이코드는 보이는 것보다 영리하다
        1번째 시도에서는 8080으로 연결. 실패하면 8081에 연결 시도
        그리고 이 모든일이 비동기적으로 발생한다는 것을 알고 있으면 여러 컴퓨터의 포트를 스캔하고 각 호스트의 1번째 응답 포트에 대한 알림을 받을 수 있다

        Arrays.asList("host-one", "host-two", "host-three")
              .stream()
              .forEach(host -> executor.getWithRetry(ctx -> new Socket(host, 8080 + ctx.getRetryCount()))
                                       .thenAccept(System.out::println));

         각 host의 RetryExecutor는 8080에 연결을 시도하고 더 높은 포트를 사용하여 재시도


         getFutureWithRetry()는 주의를 요구한다

         CompletableFuture<V>를 반환하는 메소드를 재시도할 경우
            ex. 비동기 HTTP 호출의 결과 등

         private CompletableFuture<String> asyncHttp(URL url) {}

         final CompletableFuture<CompletableFuture<String>> response = executor.getWithRetry(ctx -> asyncHttp(new URL("http://example.com")));

        getWithRetry()에 asyncHttp()를 전달하면 CompletableFuture <CompletableFuture<V> 반환

        함께 동작하는게 어색할 뿐만 아니라 부서지기도 한다
            asyncHttp()를 호출하고 실패할 경우 재시도하지만 , asyncHttp() 내부에서 실패하면 실패한다

        해결책은 간단하다
        final CompletableFuture<String> response = executor.getFutureWithRetry(ctx -> asyncHttp(new URL("http://example.com")));
        이 경우 RetryExecutor는 asyncHttp()에서 반환된것이 실제로는 Future이며 결과를 비동기적으로 기다리는 것을 이해한다


        RetryExecutor의 기본 구현은 AsyncRetryExecutor

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        RetryExecutor executor = new AsyncRetryExecutor(scheduler);

        ...

        scheduler.shutdownNow();

        대부분 하나의 thread로 충분하지만 재시도 작업을 동시에 처리하려면 pool size를 조절
        AsyncRetryExecutor는 ScheduledExecutorService를 종료하지 않는다
            -> 나중에 설명할 의식적 디자인 결정

        # RetryPolicy

        ## exception
        AbortRetryException을 제외하고는 재시도 가능하게 구성 가능
        JPA에서 OptimisticLockException로 실패한 transaction은 재시도할 수 있지만, 다른 예외는 즉시 실패

        특정 예외에 대해 다르게 설정시 retryOn() 사용
        executor.retryOn(OptimisticLockException.class)
                .withNoDelay()
                .getWithRetry(ctx -> dao.optimistic());

        특정 예외가 발생하는 경우 재시도를 중단하고 즉시 실패하도록 구성
        executor.abortOn(NullPointerException.class)
                .abortOn(IllegalArgumentException.class)
                .getWithRetry(ctx -> dao.optimistic());

        NullPointerException, IllegalArgumentException는 일시적인 오류가 아닌 bug를 나타내므로 재시도 X

        재시도 정책과 중단 정책을 결합할 수 있다
        하위 클래스에 대해 다르게 설정 가능
        executor.retryOn(IOException.class)
                .abortIf(FileNotFoundException.class)
                .retryOn(SQLException.class)
                .abortIf(DataTruncation.class)
                .getWithRetry(ctx -> dao.load(42));
        IOException, SQLException일 경우 재시도하지만 FileNotFoundException, DataTruncation라면 중단

        조건부로 제공 가능
        executor.abortIf(throwable -> throwable instanceof SQLException
                                    && throwable.getMessage().containes("ORA-00911"));

        재시도 loop를 방해하는 방법은 최대 재시도 횟수 지정
            비동기식이므로 blocking loop가 없다

        executor.withMaxRetries(5)

        # backoff - delays between retries
        실패 직후 재시도하는 것이 필요
            OptimisticLockException..
        대부분의 경우 좋지 않다. 외부 시스템에 연결할 수 없는 경우 조금만 기다리면 해결될 수 있기 때문
        CPU, bandwidth 등의 리소스 절약을 위해...
        고려해야할 점
             일정한 간격으로 재시도? 각 실패후 delay를 증가?
             대기 시간에 상한선, 하한선 필요?
             많은 작업의 재시도를 제시간에 전파하기 위해 지연시간에 임의의 jitter를 추가?

        갑자기 응답을 멈춘 바쁜 시스템의 경우 수백 ~ 수천의 요청이 실패하고 재시도된다
        이런 부하가 다시 몰려 장애를 유발할 수 있으므로
        재시도를 분산시킬 필요가 있다
        지연시간에 무작위 jitter를 추가하여 모든 요청이 정확하게 동일한 시간에 재시도되지 않도록 하는 것

        executor.withUniformJitter(100)

        ScheduledExecutorService를 사용해 작업을 실행하고 재시도를 scheduling하므로 최적의 thread 활용이 가능

        immutable을 위해 copy한다
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        AsyncRetryExecutor first = new AsyncRetryExecutor(scheduler).retryOn(Exception.class)
                                                                    .withExponentialBackoff(500, 2);

        AsyncRetryExecutor second = first.abortOn(FileNotFoundException.class);

        AsyncRetryExecutor third = second.withMaxRetries(10);

        first가 재시도될 때 second, third는 재시도 되지 않는다.
        그들은 동일한 ScheduledExecutorService를 공유
        -> AsyncRetryExecutor가 ScheduledExecutorService를 종료시키지 않는 이유
        -> 복사본이 얼마나 많은지 전혀 모르기에 life cycle을 관리하려 시도조차하지 않는다

        왜 이렇게 했을까...?

        1. concurrent code 작성시 immutability가 높으면 multi threading bug를 줄일 수 있다
            race condition, visibility가 발생할 수 없음

         */


    }
}
