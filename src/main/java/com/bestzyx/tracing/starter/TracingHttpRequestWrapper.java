package com.bestzyx.tracing.starter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import static java.util.Collections.enumeration;
import static java.util.Collections.list;

/**
 * @author zhangyongxiang
 */
public class TracingHttpRequestWrapper extends HttpServletRequestWrapper {
    
    public TracingHttpRequestWrapper(final HttpServletRequest request) {
        super(request);
    }
    
    private final Map<String, String> customHeaders = new HashMap<>();
    
    public void addHeader(final String name, final String value) {
        customHeaders.put(name, value);
    }
    
    @Override
    public String getHeader(final String name) {
        String headerValue = super.getHeader(name);
        if (customHeaders.containsKey(name)) {
            headerValue = customHeaders.get(name);
        }
        return headerValue;
    }
    
    @Override
    public Enumeration<String> getHeaderNames() {
        final List<String> names = list(super.getHeaderNames());
        names.addAll(customHeaders.keySet());
        return enumeration(new HashSet<>(names));
    }
    
    @Override
    public Enumeration<String> getHeaders(final String name) {
        final List<String> values = list(super.getHeaders(name));
        if (customHeaders.containsKey(name)) {
            values.add(customHeaders.get(name));
        }
        return enumeration(values);
    }
}