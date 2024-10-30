package com.baidu.gbi.dataengine.tracing;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * @author zhangyongxiang
 */
public class OkHttpTracingInterceptor implements Interceptor {
    
    private final TracingProperties tracingProperties;
    
    public OkHttpTracingInterceptor(final TracingProperties tracingProperties) {
        this.tracingProperties = tracingProperties;
    }
    
    @Override
    public Response intercept(final Chain chain) throws IOException {
        
        final String traceId = MDC.get(tracingProperties.getTraceKey());
        Request request = chain.request();
        if (StringUtils.hasText(traceId)) {
            request = chain.request().newBuilder()
                    .addHeader(tracingProperties.getTraceKey(), traceId)
                    .build();
        }
        return chain.proceed(request);
    }
}