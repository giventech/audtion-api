package com.audition.configuration;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class ResponseHeaderInjector implements HandlerInterceptor {

    private final Tracer tracer;

    // Constructor-based dependency injection for the OpenTelemetry tracer
    public ResponseHeaderInjector(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        // Get the current span
        Span currentSpan = Span.current();

        if (currentSpan != null) {
            // Extract the span context
            SpanContext spanContext = currentSpan.getSpanContext();

            if (spanContext.isValid()) {
                // Inject trace and span IDs into the response headers
                response.setHeader("X-Trace-Id", spanContext.getTraceId());
                response.setHeader("X-Span-Id", spanContext.getSpanId());
            }
        }
    }
}
