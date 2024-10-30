package com.baidu.gbi.dataengine.tracing;

import java.util.UUID;

import org.springframework.util.StringUtils;

/**
 * @author zhangyongxiang
 */
public class UuidThreadTraceIdGenerator implements ThreadTraceIdGenerator {
    @Override
    public String createTraceId() {
        return StringUtils.deleteAny(UUID.randomUUID().toString(), "-");
    }
}
