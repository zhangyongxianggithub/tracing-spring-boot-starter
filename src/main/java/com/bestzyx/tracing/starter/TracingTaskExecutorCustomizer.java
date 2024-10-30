package com.bestzyx.tracing.starter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import static java.util.Collections.singletonList;

/**
 * Created by zhangyongxiang on 2024/3/22 1:37 PM
 **/

public class TracingTaskExecutorCustomizer implements TaskExecutorCustomizer {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TracingTaskExecutorCustomizer.class);
    
    private final String traceKey;
    
    private final ThreadTraceIdGenerator threadTraceIdGenerator;
    
    private final List<TaskDecorator> taskDecorators;
    
    public TracingTaskExecutorCustomizer(
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
    
    public static class CompositeTaskDecorator implements TaskDecorator {
        
        private final List<TaskDecorator> taskDecorators;
        
        /**
         * Create a new instance.
         * 
         * @param taskDecorators the taskDecorators to delegate to
         */
        public CompositeTaskDecorator(
                final Collection<? extends TaskDecorator> taskDecorators) {
            Assert.notNull(taskDecorators, "TaskDecorators must not be null");
            this.taskDecorators = new ArrayList<>(taskDecorators);
        }
        
        @NonNull
        @Override
        public Runnable decorate(@NonNull Runnable runnable) {
            Assert.notNull(runnable, "Runnable must not be null");
            for (final TaskDecorator taskDecorator : this.taskDecorators) {
                runnable = taskDecorator.decorate(runnable);
            }
            return runnable;
        }
        
    }
}
