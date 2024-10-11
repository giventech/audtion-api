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
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
        final ClientHttpRequestExecution execution)
        throws IOException {
        logRequest(request, body);
        final ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(final HttpRequest request, final byte[] body) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Request method: {}, URI: {}, headers: {}, body: {}",
                request.getMethod(), request.getURI(), request.getHeaders(), new String(body));
        }
    }

    private void logResponse(final ClientHttpResponse response) throws IOException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Response status code: {}, headers: {}", response.getStatusCode(), response.getHeaders());
            logResponseBody(response);
        }
    }

    private void logResponseBody(final ClientHttpResponse response) throws IOException {
        final String contentType = response.getHeaders().getContentType() != null ?
            response.getHeaders().getContentType().toString() : "";

        if (contentType.contains("application/json")) {
            try {
                if (LOGGER.isInfoEnabled()) { // Guard log statement
                    LOGGER.info("Response body: {}", objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(objectMapper.readValue(response.getBody(), Object.class)));
                }
            } catch (IOException e) { // Catch specific exception
                LOGGER.error("Error parsing response body as JSON", e);
            }
        } else {
            if (LOGGER.isInfoEnabled()) { // Guard log statement
                LOGGER.info("Response body is not JSON, skipping parsing.");
            }
        }
    }
}
