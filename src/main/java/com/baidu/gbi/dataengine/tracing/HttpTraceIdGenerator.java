package com.baidu.gbi.dataengine.tracing;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author zhangyongxiang
 */
public interface HttpTraceIdGenerator {
    
    String createTraceId(HttpServletRequest request);
    
}
