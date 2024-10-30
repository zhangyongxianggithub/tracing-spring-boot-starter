package com.bestzyx.tracing.starter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * @author zhangyongxiang
 */
public class TracingCallable<T> implements Callable<T> {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TracingCallable.class);
    
    private final Callable<T> callable;
    
    private final Map<String, String> context;
    
    private final String traceKey;
    
    private final ThreadTraceIdGenerator threadTraceIdGenerator;
    
    public TracingCallable(final Callable<T> callable, final String traceKey,
            final ThreadTraceIdGenerator threadTraceIdGenerator) {
        this.callable = callable;
        this.context = MDC.getCopyOfContextMap();
        this.traceKey = traceKey;
        this.threadTraceIdGenerator = threadTraceIdGenerator;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "traceKey: {}, transfer MDC context {} to a wrapper runnable",
                    traceKey, this.context);
        }
    }
    
    @Override
    public T call() throws Exception {
        try {
            MDC.setContextMap(
                    Optional.ofNullable(context).orElseGet(HashMap::new));
            if (!StringUtils.hasText(MDC.get(traceKey))) {
                MDC.put(traceKey, threadTraceIdGenerator.createTraceId());
            }
            return callable.call();
        } finally {
            MDC.clear();
        }
    }
}
