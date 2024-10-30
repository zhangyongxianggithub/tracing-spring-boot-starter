package com.bestzyx.tracing.starter;

import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

/**
 * @author zhangyongxiang
 */
public class TracingRunnableDecorator implements TaskDecorator {
    
    private final String traceKey;
    
    private final ThreadTraceIdGenerator threadTraceIdGenerator;
    
    public TracingRunnableDecorator(final String traceKey,
            final ThreadTraceIdGenerator threadTraceIdGenerator) {
        this.traceKey = traceKey;
        this.threadTraceIdGenerator = threadTraceIdGenerator;
    }
    
    @NonNull
    @Override
    public Runnable decorate(@NonNull final Runnable runnable) {
        return new TracingRunnable(runnable, traceKey, threadTraceIdGenerator);
    }
}
