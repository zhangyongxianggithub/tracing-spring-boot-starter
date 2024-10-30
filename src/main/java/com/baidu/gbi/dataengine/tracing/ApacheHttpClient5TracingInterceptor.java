package com.baidu.gbi.dataengine.tracing;

import java.io.IOException;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * Created by zhangyongxiang on 2024/3/26 4:46 PM
 **/

public class ApacheHttpClient5TracingInterceptor
        implements HttpRequestInterceptor {
    
    private final TracingProperties tracingProperties;
    
    public ApacheHttpClient5TracingInterceptor(
            final TracingProperties tracingProperties) {
        this.tracingProperties = tracingProperties;
    }
    
    @Override
    public void process(final HttpRequest httpRequest,
            final EntityDetails entityDetails, final HttpContext httpContext)
            throws HttpException, IOException {
        final String traceId = MDC.get(tracingProperties.getTraceKey());
        if (StringUtils.hasText(traceId)) {
            httpRequest.addHeader(tracingProperties.getTraceKey(), traceId);
        }
    }
}
