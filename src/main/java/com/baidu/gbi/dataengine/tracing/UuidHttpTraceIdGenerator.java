package com.baidu.gbi.dataengine.tracing;

import java.util.UUID;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author zhangyongxiang
 */
public class UuidHttpTraceIdGenerator implements HttpTraceIdGenerator {
    
    @Override
    public String createTraceId(final HttpServletRequest request) {
        return StringUtils.deleteAny(UUID.randomUUID().toString(), "-");
    }
    
}
