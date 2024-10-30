package com.bestzyx.tracing.starter;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Created by zhangyongxiang on 2024/3/11 11:34 AM
 **/

class TracingAutoConfigurationTest {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TracingAutoConfigurationTest.class);
    
    @Test
    void startJob1Test() {
        new ApplicationContextRunner()
                .withPropertyValues(
                        "logging.level.com.bestzyx=debug")
                .withInitializer(ConditionEvaluationReportLoggingListener
                        .forLogLevel(LogLevel.INFO))
                .withConfiguration(
                        AutoConfigurations.of(TracingAutoConfiguration.class))
                .run((context) -> {});
    }
    
    @Test
    void startJob2Test() {
        new ApplicationContextRunner()
                .withPropertyValues(
                        "logging.level.com.bestzyx=debug")
                .withInitializer(ConditionEvaluationReportLoggingListener
                        .forLogLevel(LogLevel.INFO))
                .withConfiguration(
                        AutoConfigurations.of(TracingAutoConfiguration.class))
                .run((context) -> {});
    }
    
    @Test
    void datasourceTest() {
        new ApplicationContextRunner()
                .withPropertyValues(
                        "logging.level.com.bestzyx=debug")
                .withInitializer(ConditionEvaluationReportLoggingListener
                        .forLogLevel(LogLevel.INFO))
                .withConfiguration(
                        AutoConfigurations.of(TracingAutoConfiguration.class))
                .run((context) -> {});
    }
}
