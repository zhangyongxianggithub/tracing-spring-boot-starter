package com.baidu.gbi.dataengine.tracing;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import static java.util.Collections.singletonList;

/**
 * @author zhangyongxiang
 */
public class TracingThreadPoolTaskExecutorCustomizer
        implements ThreadPoolTaskExecutorCustomizer {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TracingThreadPoolTaskExecutorCustomizer.class);
    
    private final String traceKey;
    
    private final ThreadTraceIdGenerator threadTraceIdGenerator;
    
    private final List<TaskDecorator> taskDecorators;
    
    public TracingThreadPoolTaskExecutorCustomizer(
            final TracingProperties tracingProperties,
            final ThreadTraceIdGenerator threadTraceIdGenerator,
            final List<TaskDecorator> taskDecorators) {
        this.traceKey = tracingProperties.getTraceKey();
        this.threadTraceIdGenerator = threadTraceIdGenerator;
        this.taskDecorators = taskDecorators;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "spring application has {} task decorators, they are: {}",
                    taskDecorators.size(),
                    taskDecorators.stream().map(Object::getClass)
                            .map(Class::getCanonicalName).toList());
        }
    }
    
    @Override
    public void customize(final ThreadPoolTaskExecutor taskExecutor) {
        final var tracingRunnableDecorator = new TracingRunnableDecorator(
                this.traceKey, this.threadTraceIdGenerator);
        if (!CollectionUtils.isEmpty(taskDecorators)) {
            final var newTaskDecorators = new ArrayList<>(taskDecorators);
            newTaskDecorators.add(0, tracingRunnableDecorator);
            taskExecutor.setTaskDecorator(
                    new CompositeTaskDecorator(newTaskDecorators));
        } else {
            taskExecutor.setTaskDecorator(new CompositeTaskDecorator(
                    singletonList(tracingRunnableDecorator)));
        }
    }
}
