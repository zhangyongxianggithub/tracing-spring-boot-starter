package com.bestzyx.tracing.starter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author zhangyongxiang
 */
public interface HttpTraceIdGenerator {
    
    String createTraceId(HttpServletRequest request);
    
}
