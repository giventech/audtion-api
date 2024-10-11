package com.audition.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebServiceConfiguration implements WebMvcConfigurer {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)) // Specify Locale
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // 2. Ignore unknown properties
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE) // 3. Camel case mapping
            .setSerializationInclusion(JsonInclude.Include.NON_NULL) // 4. Exclude null values
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY) // 4. Exclude empty values
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) // 5. Do not write dates as timestamps
            .registerModule(new JavaTimeModule()); // Register JavaTimeModule

        return objectMapper; // Return configured ObjectMapper
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(createClientFactory()));

        // Configure message converter with ObjectMapper
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(objectMapper());
        restTemplate.getMessageConverters().add(messageConverter);

        // Add logging interceptor
        restTemplate.getInterceptors().add(new LoggingInterceptor());

        return restTemplate; // Return configured RestTemplate
    }

    private SimpleClientHttpRequestFactory createClientFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return requestFactory; // Return configured request factory
    }
}
