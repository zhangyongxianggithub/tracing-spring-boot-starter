package com.bestzyx.tracing.starter;

import java.util.List;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static java.util.Collections.singletonList;

/**
 * @author zhangyongxiang
 */
@ConfigurationProperties(prefix = "zyx.tracing")
@SuppressWarnings({ "LombokGetterMayBeUsed", "LombokSetterMayBeUsed" })
public class TracingProperties {
    
    /**
     * trace identify name
     */
    private String traceKey = "Trace-Id";
    
    @NestedConfigurationProperty
    private HttpTracing httpTracing = new HttpTracing();
    
    private boolean enableThreadTracing = true;
    
    public String getTraceKey() {
        return traceKey;
    }
    
    public void setTraceKey(final String traceKey) {
        this.traceKey = traceKey;
    }
    
    public HttpTracing getHttpTracing() {
        return httpTracing;
    }
    
    public void setHttpTracing(final HttpTracing httpTracing) {
        this.httpTracing = httpTracing;
    }
    
    public boolean isEnableThreadTracing() {
        return enableThreadTracing;
    }
    
    public void setEnableThreadTracing(final boolean enableThreadTracing) {
        this.enableThreadTracing = enableThreadTracing;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final TracingProperties that)) {
            return false;
        }
        return isEnableThreadTracing() == that.isEnableThreadTracing()
                && Objects.equals(getTraceKey(), that.getTraceKey())
                && Objects.equals(getHttpTracing(), that.getHttpTracing());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getTraceKey(), getHttpTracing(),
                isEnableThreadTracing());
    }
    
    @Override
    public String toString() {
        return "TracingProperties{" + "traceKey='" + traceKey + '\''
                + ", httpTracing=" + httpTracing + ", enableThreadTracing="
                + enableThreadTracing + '}';
    }
    
    public static class HttpTracing {
        
        private boolean enableHttpTracing = true;
        
        @NestedConfigurationProperty
        private Url url = new Url();
        
        public boolean isEnableHttpTracing() {
            return enableHttpTracing;
        }
        
        public void setEnableHttpTracing(final boolean enableHttpTracing) {
            this.enableHttpTracing = enableHttpTracing;
        }
        
        public Url getUrl() {
            return url;
        }
        
        public void setUrl(final Url url) {
            this.url = url;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof final HttpTracing that)) {
                return false;
            }
            return isEnableHttpTracing() == that.isEnableHttpTracing()
                    && Objects.equals(getUrl(), that.getUrl());
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(isEnableHttpTracing(), getUrl());
        }
        
        @Override
        public String toString() {
            return "HttpTracing{" + "enableHttpTracing=" + enableHttpTracing
                    + ", url=" + url + '}';
        }
    }
    
    public static class Url {
        
        private List<String> includes = singletonList("/**");
        
        public List<String> getIncludes() {
            return includes;
        }
        
        public void setIncludes(final List<String> includes) {
            this.includes = includes;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof final Url url)) {
                return false;
            }
            return Objects.equals(getIncludes(), url.getIncludes());
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(getIncludes());
        }
        
        @Override
        public String toString() {
            return "Url{" + "includes=" + includes + '}';
        }
    }
}
