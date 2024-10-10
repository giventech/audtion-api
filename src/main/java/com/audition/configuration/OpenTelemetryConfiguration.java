package com.audition.configuration;

import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfiguration {

    @Bean
    public Tracer tracer() {
        // Create your SpanExporter here, e.g., a LoggingSpanExporter
        SpanExporter loggingSpanExporter = new LoggingSpanExporter();

        // Build the SdkTracerProvider and add the exporter
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(loggingSpanExporter))
            .build();

        // Initialize OpenTelemetry SDK and set it globally
        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .buildAndRegisterGlobal();

        // Return the Tracer instance
        return openTelemetrySdk.getTracer("com.audition.configuration.ResponseHeaderInjector");
    }
}
