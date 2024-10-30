package com.baidu.gbi.dataengine.tracing;

import java.io.IOException;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author zhangyongxiang
 */
public class HttpTracingFilter extends OncePerRequestFilter implements Ordered {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(HttpTracingFilter.class);
    
    private final TracingProperties tracingProperties;
    
    private final HttpTraceIdGenerator httpTraceIdGenerator;
    
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    
    private final Predicate<HttpServletRequest> antPathPredicate;
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
    
    public HttpTracingFilter(final TracingProperties tracingProperties,
            final HttpTraceIdGenerator httpTraceIdGenerator) {
        this.tracingProperties = tracingProperties;
        this.httpTraceIdGenerator = httpTraceIdGenerator;
        this.antPathPredicate = httpRequest -> this.tracingProperties
                .getHttpTracing().getUrl().getIncludes().stream()
                .anyMatch(antPath -> this.antPathMatcher.match(antPath,
                        httpRequest.getRequestURI()));
        LOGGER.info("tracing properties: {}", tracingProperties);
    }
    
    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain)
            throws ServletException, IOException {
        if (this.antPathPredicate.test(request)) {
            String traceId = request.getHeader(tracingProperties.getTraceKey());
            try {
                if (!StringUtils.hasText(traceId)) {
                    final var requestWrapper = new TracingHttpRequestWrapper(
                            request);
                    traceId = httpTraceIdGenerator.createTraceId(request);
                    requestWrapper.addHeader(tracingProperties.getTraceKey(),
                            traceId);
                    LOGGER.warn(
                            "create a new trace id for request {}, traceId: {}",
                            request.getRequestURI(), traceId);
                }
                LOGGER.info("request {}, traceId: {}", request.getRequestURI(),
                        traceId);
                MDC.put(tracingProperties.getTraceKey(), traceId);
                filterChain.doFilter(request, response);
            } finally {
                MDC.remove(tracingProperties.getTraceKey());
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("request {} doesn't need a traceId",
                        request.getRequestURI());
            }
            filterChain.doFilter(request, response);
        }
    }
}
