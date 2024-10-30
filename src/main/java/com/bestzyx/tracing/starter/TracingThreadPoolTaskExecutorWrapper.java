package com.bestzyx.tracing.starter;

import java.io.Serial;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Created by zhangyongxiang on 2024/3/22 2:51 PM
 **/

public class TracingThreadPoolTaskExecutorWrapper
        extends ThreadPoolTaskExecutor {
    
    @Serial
    private static final long serialVersionUID = -3774032900312827542L;
    
    private final String traceKey;
    
    private final TaskDecorator taskDecorator;
    
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    
    private final UuidThreadTraceIdGenerator threadTraceIdGenerator = new UuidThreadTraceIdGenerator();
    
    public TracingThreadPoolTaskExecutorWrapper(
            final ThreadPoolTaskExecutor threadPoolTaskExecutor,
            final String traceKey) {
        this.traceKey = traceKey;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.taskDecorator = new TracingRunnableDecorator(traceKey,
                threadTraceIdGenerator);
    }
    
    public static ThreadPoolTaskExecutor wrapIfEnableTracing(
            final ThreadPoolTaskExecutor threadPoolTaskExecutor,
            final TracingProperties tracingProperties) {
        if (tracingProperties.isEnableThreadTracing()) {
            return new TracingThreadPoolTaskExecutorWrapper(
                    threadPoolTaskExecutor, tracingProperties.getTraceKey());
        } else {
            return threadPoolTaskExecutor;
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
        return threadPoolTaskExecutor.submit(this.getCallableWrapper(task));
    }
    
    @NonNull
    @Override
    public Future<?> submit(@NonNull final Runnable task) {
        return threadPoolTaskExecutor.submit(this.taskDecorator.decorate(task));
    }
    
    @Override
    public void execute(@NonNull final Runnable command) {
        threadPoolTaskExecutor.execute(this.taskDecorator.decorate(command));
    }
    
    @Override
    public void execute(@NonNull final Runnable task, final long startTimeout) {
        threadPoolTaskExecutor.execute(this.taskDecorator.decorate(task),
                startTimeout);
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
        threadPoolTaskExecutor.shutdown();
    }
    
    @Override
    public void setCorePoolSize(final int corePoolSize) {
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
    }
    
    @Override
    public int getCorePoolSize() {
        return threadPoolTaskExecutor.getCorePoolSize();
    }
    
    @Override
    public void setMaxPoolSize(final int maxPoolSize) {
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
    }
    
    @Override
    public int getMaxPoolSize() {
        return threadPoolTaskExecutor.getMaxPoolSize();
    }
    
    @Override
    public void setKeepAliveSeconds(final int keepAliveSeconds) {
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
    }
    
    @Override
    public int getKeepAliveSeconds() {
        return threadPoolTaskExecutor.getKeepAliveSeconds();
    }
    
    @Override
    public void setQueueCapacity(final int queueCapacity) {
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
    }
    
    @Override
    public int getQueueCapacity() {
        return threadPoolTaskExecutor.getQueueCapacity();
    }
    
    @Override
    public void setAllowCoreThreadTimeOut(
            final boolean allowCoreThreadTimeOut) {
        threadPoolTaskExecutor
                .setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
    }
    
    @Override
    public void setPrestartAllCoreThreads(
            final boolean prestartAllCoreThreads) {
        threadPoolTaskExecutor
                .setPrestartAllCoreThreads(prestartAllCoreThreads);
    }
    
    @NonNull
    @Override
    public void setTaskDecorator(final TaskDecorator taskDecorator) {
        threadPoolTaskExecutor.setTaskDecorator(taskDecorator);
    }
    
    @NonNull
    @Override
    public ThreadPoolExecutor getThreadPoolExecutor()
            throws IllegalStateException {
        return threadPoolTaskExecutor.getThreadPoolExecutor();
    }
    
    @Override
    public int getPoolSize() {
        return threadPoolTaskExecutor.getPoolSize();
    }
    
    @Override
    public int getQueueSize() {
        return threadPoolTaskExecutor.getQueueSize();
    }
    
    @Override
    public int getActiveCount() {
        return threadPoolTaskExecutor.getActiveCount();
    }
    
    @NonNull
    @Override
    public ListenableFuture<?> submitListenable(@NonNull final Runnable task) {
        return threadPoolTaskExecutor
                .submitListenable(this.taskDecorator.decorate(task));
    }
    
    @NonNull
    @Override
    public <T> ListenableFuture<T> submitListenable(
            @NonNull final Callable<T> task) {
        return threadPoolTaskExecutor
                .submitListenable(getCallableWrapper(task));
    }
    
    @Override
    public boolean prefersShortLivedTasks() {
        return threadPoolTaskExecutor.prefersShortLivedTasks();
    }
    
    @NonNull
    @Override
    public CompletableFuture<Void> submitCompletable(
            @NonNull final Runnable task) {
        return threadPoolTaskExecutor
                .submitCompletable(this.taskDecorator.decorate(task));
    }
    
    @NonNull
    @Override
    public <T> CompletableFuture<T> submitCompletable(
            @NonNull final Callable<T> task) {
        return threadPoolTaskExecutor
                .submitCompletable(this.getCallableWrapper(task));
    }
    
    @Override
    public void setThreadFactory(final ThreadFactory threadFactory) {
        threadPoolTaskExecutor.setThreadFactory(threadFactory);
    }
    
    @Override
    public void setThreadNamePrefix(final String threadNamePrefix) {
        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
    }
    
    @Override
    public void setRejectedExecutionHandler(
            final RejectedExecutionHandler rejectedExecutionHandler) {
        threadPoolTaskExecutor
                .setRejectedExecutionHandler(rejectedExecutionHandler);
    }
    
    @Override
    public void setAcceptTasksAfterContextClose(
            final boolean acceptTasksAfterContextClose) {
        threadPoolTaskExecutor
                .setAcceptTasksAfterContextClose(acceptTasksAfterContextClose);
    }
    
    @Override
    public void setWaitForTasksToCompleteOnShutdown(
            final boolean waitForJobsToCompleteOnShutdown) {
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(
                waitForJobsToCompleteOnShutdown);
    }
    
    @Override
    public void setAwaitTerminationSeconds(final int awaitTerminationSeconds) {
        threadPoolTaskExecutor
                .setAwaitTerminationSeconds(awaitTerminationSeconds);
    }
    
    @Override
    public void setAwaitTerminationMillis(final long awaitTerminationMillis) {
        threadPoolTaskExecutor
                .setAwaitTerminationMillis(awaitTerminationMillis);
    }
    
    @Override
    public void setPhase(final int phase) {
        threadPoolTaskExecutor.setPhase(phase);
    }
    
    @Override
    public int getPhase() {
        return threadPoolTaskExecutor.getPhase();
    }
    
    @Override
    public void setBeanName(@NonNull final String name) {
        threadPoolTaskExecutor.setBeanName(name);
    }
    
    @Override
    public void setApplicationContext(
            @NonNull final ApplicationContext applicationContext) {
        threadPoolTaskExecutor.setApplicationContext(applicationContext);
    }
    
    @Override
    public void afterPropertiesSet() {
        threadPoolTaskExecutor.afterPropertiesSet();
    }
    
    @Override
    public void initialize() {
        threadPoolTaskExecutor.initialize();
    }
    
    @Override
    public void destroy() {
        threadPoolTaskExecutor.destroy();
    }
    
    @Override
    public void initiateShutdown() {
        threadPoolTaskExecutor.initiateShutdown();
    }
    
    @Override
    public void start() {
        threadPoolTaskExecutor.start();
    }
    
    @Override
    public void stop() {
        threadPoolTaskExecutor.stop();
    }
    
    @Override
    public void stop(@NonNull final Runnable callback) {
        threadPoolTaskExecutor.stop(callback);
    }
    
    @Override
    public boolean isRunning() {
        return threadPoolTaskExecutor.isRunning();
    }
    
    @Override
    public void onApplicationEvent(@NonNull final ContextClosedEvent event) {
        threadPoolTaskExecutor.onApplicationEvent(event);
    }
    
    @Override
    public boolean supportsAsyncExecution() {
        return threadPoolTaskExecutor.supportsAsyncExecution();
    }
    
    @Override
    public boolean isAutoStartup() {
        return threadPoolTaskExecutor.isAutoStartup();
    }
    
    @NonNull
    @Override
    public Thread newThread(@NonNull final Runnable runnable) {
        return threadPoolTaskExecutor.newThread(runnable);
    }
    
    @NonNull
    @Override
    public String getThreadNamePrefix() {
        return threadPoolTaskExecutor.getThreadNamePrefix();
    }
    
    @Override
    public void setThreadPriority(final int threadPriority) {
        threadPoolTaskExecutor.setThreadPriority(threadPriority);
    }
    
    @Override
    public int getThreadPriority() {
        return threadPoolTaskExecutor.getThreadPriority();
    }
    
    @Override
    public void setDaemon(final boolean daemon) {
        threadPoolTaskExecutor.setDaemon(daemon);
    }
    
    @Override
    public boolean isDaemon() {
        return threadPoolTaskExecutor.isDaemon();
    }
    
    @Override
    public void setThreadGroupName(@NonNull final String name) {
        threadPoolTaskExecutor.setThreadGroupName(name);
    }
    
    @Override
    public void setThreadGroup(final ThreadGroup threadGroup) {
        threadPoolTaskExecutor.setThreadGroup(threadGroup);
    }
    
    @Override
    public ThreadGroup getThreadGroup() {
        return threadPoolTaskExecutor.getThreadGroup();
    }
    
    @NonNull
    @Override
    public Thread createThread(@NonNull final Runnable runnable) {
        return threadPoolTaskExecutor.createThread(runnable);
    }
}
