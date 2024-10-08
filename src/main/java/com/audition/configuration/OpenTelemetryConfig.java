//package com.audition.configuration;
//
//import io.opentelemetry.api.GlobalOpenTelemetry;
//import io.opentelemetry.api.trace.Tracer;
//import io.opentelemetry.exporter.logging.LoggingSpanExporter;
//import io.opentelemetry.sdk.OpenTelemetrySdk;
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
//        // Use LoggingSpanExporter for console output
//        LoggingSpanExporter exporter = LoggingSpanExporter.create();
//        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
//            .addSpanProcessor(SimpleSpanProcessor.create(exporter))
//            .build();
//
//        OpenTelemetrySdk.builder()
//            .setTracerProvider(tracerProvider)
//            .buildAndRegisterGlobal();
//
//        return GlobalOpenTelemetry.getTracer("your.tracer.name");
//    }
//}
