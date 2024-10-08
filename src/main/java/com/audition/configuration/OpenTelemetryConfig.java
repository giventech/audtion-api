//package com.audition.configuration;
//
//import io.opentelemetry.api.GlobalOpenTelemetry;
//import io.opentelemetry.api.trace.Tracer;
//import io.opentelemetry.exporter.logging.LoggingSpanExporter;
//import io.opentelemetry.sdk.OpenTelemetrySdk;
//import io.opentelemetry.sdk.OpenTelemetrySdkBuilder;
//import io.opentelemetry.sdk.trace.SdkTracerProvider;
//import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OpenTelemetryConfig {
//
//    @Bean
//    public Tracer openTelemetryTracer() {
//        // Create a LoggingSpanExporter to log spans to the console
//        LoggingSpanExporter exporter = LoggingSpanExporter.create();
//
//        // Build and configure the SdkTracerProvider
//        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
//            .addSpanProcessor(SimpleSpanProcessor.create(exporter))
//            .build();
//
//        // Use OpenTelemetrySdkBuilder.buildAndRegisterGlobal
//        OpenTelemetrySdkBuilder sdkBuilder = OpenTelemetrySdk.builder()
//            .setTracerProvider(tracerProvider);
//
//        OpenTelemetrySdk sdk = sdkBuilder.buildAndRegisterGlobal();
//
//        // Return the Tracer from the globally registered OpenTelemetry instance
//        return GlobalOpenTelemetry.getTracer("com.audition.tracer");
//    }
//}
