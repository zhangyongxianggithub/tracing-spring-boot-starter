package com.baidu.gbi.dataengine.tracing;

/**
 * @author zhangyongxiang
 */
@FunctionalInterface
public interface ThreadTraceIdGenerator {
    
    String createTraceId();
    
}
