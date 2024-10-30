package com.bestzyx.tracing.starter;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * @author zhangyongxiang
 */
public class RestTemplateTracingInterceptor
        implements ClientHttpRequestInterceptor {
    
    private final TracingProperties tracingProperties;
    
    public RestTemplateTracingInterceptor(
            final TracingProperties tracingProperties) {
        this.tracingProperties = tracingProperties;
    }
    
    @NonNull
    @Override
    public ClientHttpResponse intercept(@NonNull final HttpRequest request,
            @NonNull final byte[] body,
            @NonNull final ClientHttpRequestExecution execution)
            throws IOException {
        final String traceId = MDC.get(tracingProperties.getTraceKey());
        if (StringUtils.hasText(traceId)) {
            request.getHeaders().add(tracingProperties.getTraceKey(), traceId);
        }
        
        return execution.execute(request, body);
    }
}
