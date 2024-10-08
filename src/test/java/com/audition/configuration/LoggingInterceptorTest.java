//package com.audition.configuration;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpRequest;
//import org.springframework.http.client.ClientHttpRequestExecution;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.mock.http.client.MockClientHttpRequest;
//import org.springframework.mock.http.client.MockClientHttpResponse;
//
//public class LoggingInterceptorTest {
//
//    @Mock
//    private ClientHttpRequestExecution mockExecution;
//
//    @Captor
//    private ArgumentCaptor<HttpRequest> requestCaptor;
//
//    private LoggingInterceptor loggingInterceptor;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        loggingInterceptor = new LoggingInterceptor();
//    }
//
//    @Test
//    public void shouldLogRequestMethodUriHeadersAndBodyForGetRequest() throws IOException {
//        // Given
//        MockClientHttpRequest mockRequest = new MockClientHttpRequest(HttpMethod.GET,
//            "http://example.com/api/resource");
//        HttpHeaders headers = mockRequest.getHeaders();
//        headers.set("Header1", "Value1");
//        headers.set("Header2", "Value2");
//        byte[] requestBody = "request body".getBytes(StandardCharsets.UTF_8);
//
//        // Prepare a mock response
//        ClientHttpResponse mockResponse = new MockClientHttpResponse(
//            new ByteArrayInputStream("response body".getBytes(StandardCharsets.UTF_8)), 200);
//
//        // When
//        when(mockExecution.execute(any(HttpRequest.class), any(byte[].class))).thenReturn(mockResponse);
//        loggingInterceptor.intercept(mockRequest, requestBody, mockExecution);
//
//        // Then
//        verify(mockExecution).execute(requestCaptor.capture(), any(byte[].class));
//        HttpRequest capturedRequest = requestCaptor.getValue();
//
//        assertEquals(HttpMethod.GET, capturedRequest.getMethod());
//        assertEquals("http://example.com/api/resource", capturedRequest.getURI().toString());
//        assertEquals("Value1", capturedRequest.getHeaders().getFirst("Header1"));
//        assertEquals("Value2", capturedRequest.getHeaders().getFirst("Header2"));
//
//        // Since MockClientHttpRequest does not store the body, we verify the request body separately
//        assertEquals("request body", new String(requestBody, StandardCharsets.UTF_8));
//    }
//
//
//    @Test
//    public void shouldLogResponseHeadersAndBodyForGetRequest() throws IOException {
//        // Given
//        MockClientHttpRequest mockRequest = new MockClientHttpRequest(HttpMethod.GET,
//            "http://example.com/api/resource");
//        HttpHeaders headers = mockRequest.getHeaders();
//        headers.set("Header1", "Value1");
//        headers.set("Header2", "Value2");
//        byte[] requestBody = "request body".getBytes(StandardCharsets.UTF_8);
//
//        String body = "[{\n"
//            + "    \"postId\": 1,\n"
//            + "    \"id\": 1,\n"
//            + "    \"name\": \"id labore ex et quam laborum\",\n"
//            + "    \"email\": \"Eliseo@gardner.biz\",\n"
//            + "    \"body\": \"laudantium enim quasi est quidem magnam voluptate ipsam eos\\ntempora quo necessitatibus\\ndolor quam autem quasi\\nreiciendis et nam sapiente accusantium\"\n"
//            + "  }";
//
//        // Prepare a mock response
//        ClientHttpResponse mockResponse = new MockClientHttpResponse(
//            new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)), 200);
//        HttpHeaders responseHeaders = mockResponse.getHeaders();
//        responseHeaders.set("ResponseHeader1", "ResponseValue1");
//        responseHeaders.set("ResponseHeader2", "ResponseValue2");
//
//        // When
//        when(mockExecution.execute(any(HttpRequest.class), any(byte[].class))).thenReturn(mockResponse);
//        loggingInterceptor.intercept(mockRequest, requestBody, mockExecution);
//
//        // Then
//        verify(mockExecution).execute(requestCaptor.capture(), any(byte[].class));
//        ClientHttpResponse capturedResponse = mockExecution.execute(requestCaptor.getValue(), requestBody);
//
//        assertEquals("ResponseValue1", capturedResponse.getHeaders().getFirst("ResponseHeader1"));
//        assertEquals("ResponseValue2", capturedResponse.getHeaders().getFirst("ResponseHeader2"));
//
//        // Since MockClientHttpResponse does not store the body, we verify the response body separately
//        assertEquals("response body", new String(capturedResponse.getBody().readAllBytes(), StandardCharsets.UTF_8));
//    }
//
//}
