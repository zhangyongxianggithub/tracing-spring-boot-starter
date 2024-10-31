package com.bestzyx.tracing.starter;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

/**
 * @author zhangyongxiang
 */
@Configuration
@AutoConfigureAfter(TaskExecutionAutoConfiguration.class)
@EnableConfigurationProperties({ TracingProperties.class })
public class TracingAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "zyx.tracing.http-tracing",
            name = "enable-http-tracing", matchIfMissing = true)
    public HttpTracingFilter httpTracingFilter(
            final TracingProperties tracingProperties,
            final HttpTraceIdGenerator httpTraceIdGenerator) {
        return new HttpTracingFilter(tracingProperties, httpTraceIdGenerator);
    }
    
    @Configuration
    @ConditionalOnProperty(prefix = "zyx.tracing",
            name = "enable-thread-tracing", matchIfMissing = true)
    @ConditionalOnClass(
            name = "org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer")
    static class SpringThreadPoolTaskConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public TracingThreadPoolTaskExecutorCustomizer tracingThreadPoolTaskExecutorCustomizer(
                final TracingProperties tracingProperties,
                final ThreadTraceIdGenerator threadTraceIdGenerator,
                final List<TaskDecorator> taskDecorators) {
            return new TracingThreadPoolTaskExecutorCustomizer(
                    tracingProperties, threadTraceIdGenerator, taskDecorators);
        }
    }
    
    @Configuration
    @ConditionalOnProperty(prefix = "zyx.tracing",
            name = "enable-thread-tracing", matchIfMissing = true)
    @ConditionalOnMissingClass("org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer")
    @ConditionalOnClass(
            name = "org.springframework.boot.task.TaskExecutorCustomizer")
    static class SpringTaskExecutorConfiguration {
        
        @Bean
        @ConditionalOnMissingBean
        public TracingTaskExecutorCustomizer tracingTaskExecutorCustomizer(
                final TracingProperties tracingProperties,
                final ThreadTraceIdGenerator threadTraceIdGenerator,
                final List<TaskDecorator> taskDecorators) {
            return new TracingTaskExecutorCustomizer(tracingProperties,
                    threadTraceIdGenerator, taskDecorators);
        }
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "zyx.tracing.http-tracing",
            name = "enable-http-tracing", matchIfMissing = true)
    public HttpTraceIdGenerator httpTraceIdGenerator() {
        return new UuidHttpTraceIdGenerator();
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "zyx.tracing",
            name = "enable-thread-tracing", matchIfMissing = true)
    public ThreadTraceIdGenerator threadTraceIdGenerator() {
        return new UuidThreadTraceIdGenerator();
    }
    
    @Configuration
    @ConditionalOnClass(name = "org.apache.http.HttpRequestInterceptor")
    @ConditionalOnProperty(prefix = "zyx.tracing.http-tracing",
            name = "enable-http-tracing", matchIfMissing = true)
    static class ApacheHttpClientConfiguration {
        
        @Bean
        public ApacheHttpClientTracingInterceptor apacheHttpClientTracingInterceptor(
                final TracingProperties tracingProperties) {
            return new ApacheHttpClientTracingInterceptor(tracingProperties);
        }
    }
    
    @Configuration
    @ConditionalOnClass(
            name = "org.apache.hc.core5.http.HttpRequestInterceptor")
    @ConditionalOnProperty(prefix = "zyx.tracing.http-tracing",
            name = "enable-http-tracing", matchIfMissing = true)
    static class ApacheHttpClient5Configuration {
        
        @Bean
        public ApacheHttpClient5TracingInterceptor apacheHttpClient5TracingInterceptor(
                final TracingProperties tracingProperties) {
            return new ApacheHttpClient5TracingInterceptor(tracingProperties);
        }
    }
    
    @Configuration
    @ConditionalOnClass(name = "com.squareup.okhttp.Interceptor")
    @ConditionalOnProperty(prefix = "zyx.tracing.http-tracing",
            name = "enable-http-tracing", matchIfMissing = true)
    static class OkHttpConfiguration {
        @Bean
        public OkHttpTracingInterceptor okHttpTracingInterceptor(
                final TracingProperties tracingProperties) {
            return new OkHttpTracingInterceptor(tracingProperties);
        }
        
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "zyx.tracing.http-tracing",
            name = "enable-http-tracing", matchIfMissing = true)
    public RestTemplateTracingInterceptor restTemplateTracingInterceptor(
            final TracingProperties tracingProperties) {
        return new RestTemplateTracingInterceptor(tracingProperties);
    }
    
}
