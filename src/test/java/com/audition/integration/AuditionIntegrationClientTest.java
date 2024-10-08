package com.audition.integration;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.common.util.AuditionConstants;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import com.audition.model.Comment;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class AuditionIntegrationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    public String mockAuditionAPIUrl;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);

        // Get the field representing the auditionAPIUrl property
        Field field = AuditionIntegrationClient.class.getDeclaredField("auditionAPIUrl");
        field.setAccessible(true);

        // Set a mock value for the auditionAPIUrl property
        mockAuditionAPIUrl = "https://jsonplaceholder.typicode.com";
        field.set(auditionIntegrationClient, mockAuditionAPIUrl);
    }


    @Test
    public void shouldReturnEmptyListWhenNoPostsInAuditionAPI() {

        when(restTemplate.exchange(
            eq(mockAuditionAPIUrl + "/posts"),
            eq(HttpMethod.GET),
            any(),
            any(ParameterizedTypeReference.class))) // Add a matcher for the ParameterizedTypeReference
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        SystemException exception = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts());

        assertEquals("Client error occurred (status 404 NOT_FOUND): 404 NOT_FOUND", exception.getMessage());
        assertEquals(404, exception.getStatusCode(), 404);
    }


    @Test
    public void shouldThrowInternalServerErrorWhenAuditionAPIIsUnavailable() {
        // Fix when to cater for exchange method

        when(restTemplate.exchange(
            eq(mockAuditionAPIUrl + "/posts"),
            eq(HttpMethod.GET),
            any(),
            any(ParameterizedTypeReference.class))) // Add a matcher for the ParameterizedTypeReference
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        SystemException exception = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts());

        assertEquals("Client error occurred (status 500 INTERNAL_SERVER_ERROR): 500 INTERNAL_SERVER_ERROR",
            exception.getMessage());
        assertEquals(500, exception.getStatusCode(), 500);
    }


    @Test
    public void shouldThrowServiceUnavailableWhenAuditionAPIIsUnavailable() {
        when(restTemplate.exchange(
            eq(mockAuditionAPIUrl + "/posts"),
            eq(HttpMethod.GET),
            any(),
            any(ParameterizedTypeReference.class))) // Add a matcher for the ParameterizedTypeReference
            .thenThrow(new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE));

        SystemException exception = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts());

        assertEquals("Client error occurred (status 503 SERVICE_UNAVAILABLE): 503 SERVICE_UNAVAILABLE",
            exception.getMessage());
        assertEquals(503, exception.getStatusCode(), 503);
    }


    @Test
    public void shouldReturnAllPosts() {
        // Given
        AuditionIntegrationClient client = Mockito.mock(AuditionIntegrationClient.class);
        List<AuditionPost> expectedPosts = Arrays.asList(new AuditionPost(), new AuditionPost());
        Mockito.when(client.getPosts()).thenReturn(expectedPosts);

        // When
        List<AuditionPost> actualPosts = client.getPosts();

        // Then
        assertEquals(expectedPosts.size(), actualPosts.size());
    }

    @Test
    void shouldReturnValidPostObjectWhenIdIsValidInteger() {
        // Given
        String postId = "1";
        AuditionPost expectedPost = new AuditionPost();
        expectedPost.setId(1);
        expectedPost.setUserId(1);
        expectedPost.setTitle("Title");
        expectedPost.setBody("Body");
        // When
        when(restTemplate.getForObject(anyString(), eq(AuditionPost.class))).thenReturn(expectedPost);

        // Then
        AuditionPost actualPost = auditionIntegrationClient.getPostById(postId);
        assertNotNull(actualPost);
        assertEquals(expectedPost.getId(), actualPost.getId());
        assertEquals(expectedPost.getUserId(), actualPost.getUserId());
        assertEquals(expectedPost.getTitle(), actualPost.getTitle());
        assertEquals(expectedPost.getBody(), actualPost.getBody());
    }

    @Test
    void shouldRaiseSystemExceptionWhenPostNotFound() {
        // Given
        String postId = "1";
        HttpClientErrorException notFoundException = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(restTemplate.getForObject(eq(mockAuditionAPIUrl + "/posts/" + postId), eq(AuditionPost.class)))
            .thenThrow(notFoundException);

        // When
        Throwable thrown = catchThrowable(() -> auditionIntegrationClient.getPostById(postId));

        // Then
        assertThat(thrown).isInstanceOf(SystemException.class)
            .hasMessage(AuditionConstants.NO_RECORD_FOUND + "1")
            .hasFieldOrPropertyWithValue("title", "Resource Not Found")
            .hasFieldOrPropertyWithValue("statusCode", 404);
    }

    @Test
    void shouldRaiseAnInternalServerErrorWhenCallToPostsAPIFails() {
        String postId = "1";
        // Given
        HttpClientErrorException restClientException = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForObject(eq(mockAuditionAPIUrl + "/posts/" + postId), eq(AuditionPost.class)))
            .thenThrow(restClientException);

        // when
        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById(postId));

        // Then
        assertEquals(AuditionConstants.ERROR_RETRIEVING_POST, exception.getMessage());
        assertEquals("500", String.valueOf(exception.getStatusCode()));
    }


    @Test
    public void shouldReturnAPostWithItsComments() {
        // Given
        int postId = 1;
        AuditionPost mockPost = new AuditionPost();
        mockPost.setId(postId);
        mockPost.setTitle("Test Post");
        mockPost.setBody("This is a test post body");

        Comment mockComment = new Comment();
        mockComment.setId(1);
        mockComment.setPostId(postId);
        mockComment.setName("Test Comment");
        mockComment.setBody("This is a test comment body");
        mockComment.setEmail("test@example.com");

        List<Comment> mockComments = Collections.singletonList(mockComment);

        // Mock the behavior of the RestTemplate

        when(restTemplate.getForObject(anyString(), eq(AuditionPost.class))).thenReturn(mockPost);
        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(mockComments);

        // When
        AuditionPostComments result = auditionIntegrationClient.getPostWithComments(String.valueOf(postId));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertEquals(postId, result.getPost().getId());
        assertEquals("Test Post", result.getPost().getTitle());
        assertEquals("This is a test post body", result.getPost().getBody());
        assertEquals(mockComment.getId(), result.getComments().get(0).getId());
        assertEquals(mockComments, result.getComments());
    }


    @Test
    public void shouldReturnANumberOfComments() {
        // Given
        List<Comment> mockComments;
        mockComments = Arrays.asList(new Comment(), new Comment(), new Comment());

        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(mockComments);

        // When
        List<Comment> comments = auditionIntegrationClient.getCommentsByPostId(1);

        // Then
        assertEquals(3, comments.size());
    }


    @Test
    public void shouldThrowServerExceptionWhenWhenGettingCommentsThrowsHttpServerException() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(List.class)))
            .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"));

        // Act & Assert
        assertThrows(SystemException.class, () -> auditionIntegrationClient.getCommentsByPostId(1));
    }
}