package com.audition.configuration;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
// TODO Inject openTelemetry trace and span Ids in the response headers.

public class ResponseHeaderInjector extends OncePerRequestFilter {

    private final Tracer tracer;

    public ResponseHeaderInjector() {
        // Initialize the Tracer
        this.tracer = GlobalOpenTelemetry.getTracer("your.tracer.name"); // Replace with your tracer name
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        Span span = Span.fromContext(Context.current());

        // Inject trace and span IDs into response headers
        response.addHeader("X-Trace-Id", span.getSpanContext().getTraceId());
        response.addHeader("X-Span-Id", span.getSpanContext().getSpanId());

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }


}
