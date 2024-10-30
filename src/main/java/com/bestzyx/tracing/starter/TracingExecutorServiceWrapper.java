package com.bestzyx.tracing.starter;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

import static java.util.Collections.emptyList;

/**
 * @author zhangyongxiang
 */
public class TracingExecutorServiceWrapper implements ExecutorService {
    
    private final String traceKey;
    
    private final TaskDecorator taskDecorator;
    
    private final ExecutorService executorService;
    
    private final UuidThreadTraceIdGenerator threadTraceIdGenerator = new UuidThreadTraceIdGenerator();
    
    public TracingExecutorServiceWrapper(final ExecutorService executorService,
            final String traceKey) {
        this.traceKey = traceKey;
        this.executorService = executorService;
        this.taskDecorator = new TracingRunnableDecorator(traceKey,
                threadTraceIdGenerator);
    }
    
    public static ExecutorService wrapIfEnableTracing(
            final ExecutorService executorService,
            final TracingProperties tracingProperties) {
        if (tracingProperties.isEnableThreadTracing()) {
            return new TracingExecutorServiceWrapper(executorService,
                    tracingProperties.getTraceKey());
        } else {
            return executorService;
        }
        
    }
    
    /**
     * 提交Callable任务，并返回Future对象。
     *
     * @param task 要提交的Callable任务
     * @return 返回一个Future对象，用于获取任务执行结果
     */
    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull final Callable<T> task) {
        return executorService.submit(this.getCallableWrapper(task));
    }
    
    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull final Runnable task, final T result) {
        return executorService.submit(this.taskDecorator.decorate(task),
                result);
    }
    
    @NonNull
    @Override
    public Future<?> submit(@NonNull final Runnable task) {
        return executorService.submit(this.taskDecorator.decorate(task));
    }
    
    /**
     * 使用CallableWrapper调用所有Callable任务，并返回Future结果列表
     *
     * @param tasks 包含Callable任务的集合
     * @return 返回一个包含Future对象的列表
     */
    @NonNull
    @Override
    public <T> List<Future<T>> invokeAll(
            @NonNull final Collection<? extends Callable<T>> tasks)
            throws InterruptedException {
        if (!tasks.isEmpty()) {
            return executorService.invokeAll(
                    tasks.stream().map(this::getCallableWrapper).toList());
        }
        return emptyList();
    }
    
    @NonNull
    @Override
    public <T> List<Future<T>> invokeAll(
            @NonNull final Collection<? extends Callable<T>> tasks,
            final long timeout, @NonNull final TimeUnit unit)
            throws InterruptedException {
        if (!tasks.isEmpty()) {
            return executorService.invokeAll(
                    tasks.stream().map(this::getCallableWrapper).toList(),
                    timeout, unit);
        }
        return emptyList();
    }
    
    /**
     * 执行任务并返回结果。
     * 该方法会将指定的任务集合转化为CallableWrapper，并调用父类的invokeAny方法执行任务，最后将返回值进行转换。
     *
     * @param tasks 指定的任务集合
     * @return 执行完所有任务后的返回值
     */
    @NonNull
    @Override
    public <T> T invokeAny(
            @NonNull final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return executorService.invokeAny(
                tasks.stream().map(this::getCallableWrapper).toList());
    }
    
    @Override
    public <T> T invokeAny(
            @NonNull final Collection<? extends Callable<T>> tasks,
            final long timeout, @NonNull final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return executorService.invokeAny(
                tasks.stream().map(this::getCallableWrapper).toList(), timeout,
                unit);
    }
    
    /**
     * 执行Runnable任务。
     *
     * @param command 需要执行的Runnable命令
     */
    @Override
    public void execute(@NonNull final Runnable command) {
        executorService.execute(this.taskDecorator.decorate(command));
    }
    
    /**
     * 获取CallableWrapper
     *
     * @param task 任务
     * @return Callable包装器
     */
    @NonNull
    private <T> Callable<T> getCallableWrapper(
            @NonNull final Callable<T> task) {
        return new TracingCallable<>(task, traceKey, threadTraceIdGenerator);
    }
    
    @Override
    public void shutdown() {
        executorService.shutdown();
    }
    
    @NonNull
    @Override
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }
    
    @Override
    public boolean isShutdown() {
        return executorService.isShutdown();
    }
    
    @Override
    public boolean isTerminated() {
        return executorService.isTerminated();
    }
    
    @Override
    public boolean awaitTermination(final long timeout,
            @NonNull final TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }
    
}
