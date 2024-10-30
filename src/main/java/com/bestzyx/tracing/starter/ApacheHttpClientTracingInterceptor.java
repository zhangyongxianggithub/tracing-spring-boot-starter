package com.bestzyx.tracing.starter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * @author zhangyongxiang
 */
public class ApacheHttpClientTracingInterceptor
        implements HttpRequestInterceptor {
    
    private final TracingProperties tracingProperties;
    
    public ApacheHttpClientTracingInterceptor(
            final TracingProperties tracingProperties) {
        this.tracingProperties = tracingProperties;
    }
    
    @Override
    public void process(final HttpRequest httpRequest,
            final HttpContext httpContext) {
        final String traceId = MDC.get(tracingProperties.getTraceKey());
        if (StringUtils.hasText(traceId)) {
            httpRequest.addHeader(tracingProperties.getTraceKey(), traceId);
        }
    }
    
}