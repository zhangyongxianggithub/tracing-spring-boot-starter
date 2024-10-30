package com.bestzyx.tracing.starter;

/**
 * @author zhangyongxiang
 */
@FunctionalInterface
public interface ThreadTraceIdGenerator {
    
    String createTraceId();
    
}
