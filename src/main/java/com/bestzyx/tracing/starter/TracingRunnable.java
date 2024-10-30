package com.bestzyx.tracing.starter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * @author zhangyongxiang
 */
public class TracingRunnable implements Runnable {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TracingRunnable.class);
    
    private final Runnable runnable;
    
    private final Map<String, String> context;
    
    private final String traceKey;
    
    private final ThreadTraceIdGenerator threadTraceIdGenerator;
    
    public TracingRunnable(final Runnable runnable, final String traceKey,
            final ThreadTraceIdGenerator threadTraceIdGenerator) {
        this.runnable = runnable;
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
    public void run() {
        try {
            MDC.setContextMap(
                    Optional.ofNullable(context).orElseGet(HashMap::new));
            if (!StringUtils.hasText(MDC.get(traceKey))) {
                MDC.put(traceKey, threadTraceIdGenerator.createTraceId());
            }
            runnable.run();
        } finally {
            MDC.clear();
        }
        
    }
}
