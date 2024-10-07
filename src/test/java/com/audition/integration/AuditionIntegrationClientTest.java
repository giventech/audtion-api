package com.audition.integration;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.model.PostComment;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


public class AuditionIntegrationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnEmptyListWhenNoPostsInAuditionAPI() {

        when(restTemplate.getForObject(Mockito.any(String.class), any(Class.class))).thenThrow(
            new HttpClientErrorException(HttpStatus.NOT_FOUND));

        SystemException exception = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts());

        assertEquals("Client error occurred (status 404 NOT_FOUND): 404 NOT_FOUND", exception.getMessage());
        assertEquals(404, exception.getStatusCode(), 404);
    }

    @Test
    public void shouldThrowInternalServerErrorWhenAuditionAPIIsUnavailable() {
        when(restTemplate.getForObject(Mockito.any(String.class), any(Class.class))).thenThrow(
            new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        SystemException exception = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts());

        assertEquals("Client error occurred (status 500 INTERNAL_SERVER_ERROR): 500 INTERNAL_SERVER_ERROR",
            exception.getMessage());
        assertEquals(500, exception.getStatusCode(), 500);
    }


    @Test
    public void shouldThrowServiceUnavailableWhenAuditionAPIIsUnavailable() {
        when(restTemplate.getForObject(Mockito.any(String.class), any(Class.class))).thenThrow(
            new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE));

        SystemException exception = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts());

        assertEquals("Client error occurred (status 503 SERVICE_UNAVAILABLE): 503 SERVICE_UNAVAILABLE",
            exception.getMessage());
        assertEquals(503, exception.getStatusCode(), 503);
    }


    @Test
    public void testGetPosts_shouldReturnAllPosts() {
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
    public void testGetPostWithComments() {
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
        PostComment result = auditionIntegrationClient.getPostWithComments(postId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertEquals(postId, result.getPost().getId());
        assertEquals("Test Post", result.getPost().getTitle());
        assertEquals("This is a test post body", result.getPost().getBody());
        assertEquals(mockComment.getId(), result.getComments().get(0).getId());
        assertEquals(mockComments, result.getComments());
    }

}