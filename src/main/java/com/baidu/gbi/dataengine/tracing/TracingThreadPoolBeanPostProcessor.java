package com.baidu.gbi.dataengine.tracing;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static com.baidu.gbi.dataengine.tracing.TracingThreadPoolTaskExecutorWrapper.wrapIfEnableTracing;

/**
 * Created by zhangyongxiang on 2024/4/7 3:03 PM
 **/

public class TracingThreadPoolBeanPostProcessor implements BeanPostProcessor {
    
    private static final Logger LOG = LoggerFactory
            .getLogger(TracingThreadPoolBeanPostProcessor.class);
    
    private final TracingProperties tracingProperties;
    
    public TracingThreadPoolBeanPostProcessor(
            final TracingProperties tracingProperties) {
        this.tracingProperties = tracingProperties;
    }
    
    @NonNull
    @Override
    public Object postProcessBeforeInitialization(@NonNull final Object bean,
            @NonNull final String beanName) throws BeansException {
        if (bean instanceof TracingThreadPoolTaskExecutorWrapper
                || bean instanceof TracingExecutorServiceWrapper) {
            LOG.info("bean {} has been wrapped in a trace context", beanName);
            return bean;
        }
        if (bean instanceof final ThreadPoolTaskExecutor taskExecutor) {
            LOG.info(
                    "bean {} is a pure executor of type ThreadPoolTaskExecutor, wrap it with trace context",
                    beanName);
            return wrapIfEnableTracing(taskExecutor, tracingProperties);
        }
        
        if (bean instanceof final ExecutorService executorService) {
            LOG.info(
                    "bean {} is a pure executor of type ExecutorService, wrap it with trace context",
                    beanName);
            return TracingExecutorServiceWrapper
                    .wrapIfEnableTracing(executorService, tracingProperties);
        }
        return bean;
    }
}
