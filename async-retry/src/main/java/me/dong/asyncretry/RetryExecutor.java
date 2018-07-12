package me.dong.asyncretry;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by ethan.kim on 2018. 7. 12..
 */
public interface RetryExecutor {

    CompletableFuture<Void> doWithRetry(Consumer<RetryContext> consumer);

    <V> CompletableFuture<V> getWithRetry(Supplier<V> supplier);

    <V> CompletableFuture<V> getWithRetry(Function<RetryContext, V> function);
}
