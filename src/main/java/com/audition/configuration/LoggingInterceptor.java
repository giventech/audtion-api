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

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        logger.info("Request method: {}, URI: {}", request.getMethod(), request.getURI());
        logger.info("Request headers: {}", request.getHeaders());
        logger.info("Request body: {}", new String(body));

        ClientHttpResponse response = execution.execute(request, body);

        logger.info("Response status code: {}", response.getStatusCode());
        logger.info("Response headers: {}", response.getHeaders());

        response.getBody().close();  // to make sure the response is fully consumed by the caller

        // Log the response body in debug mode to avoid performance issues in production.
        // Responses are buffered in WebServiceConfiguration using BufferingClientHttpRequestFactory.

        // Only log the response body if it's JSON
        String contentType =
            response.getHeaders().getContentType() != null ? response.getHeaders().getContentType().toString() : "";

        if (contentType.contains("application/json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Object responseBody = objectMapper.readValue(response.getBody(), Object.class);
                logger.info("Response body: {}",
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseBody));
            } catch (Exception e) {
                logger.error("Error parsing response body as JSON", e);
            }
        } else {
            logger.info("Response body is not JSON, skipping parsing.");
        }

        return response;


    }
}