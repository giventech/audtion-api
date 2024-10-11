package com.audition.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Request method: {}, URI: {}, headers: {}, body: {}",
                request.getMethod(), request.getURI(), request.getHeaders(), new String(body));
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Response status code: {}, headers: {}", response.getStatusCode(), response.getHeaders());
            logResponseBody(response);
        }
    }

    private void logResponseBody(ClientHttpResponse response) throws IOException {
        String contentType = response.getHeaders().getContentType() != null ?
            response.getHeaders().getContentType().toString() : "";

        if (contentType.contains("application/json")) {
            try {
                LOGGER.info("Response body: {}", objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(objectMapper.readValue(response.getBody(), Object.class)));
            } catch (Exception e) {
                LOGGER.error("Error parsing response body as JSON", e);
            }
        } else {
            LOGGER.info("Response body is not JSON, skipping parsing.");
        }
    }
}
