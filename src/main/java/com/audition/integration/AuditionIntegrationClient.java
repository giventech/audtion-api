package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.model.PostComment;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {


    @Autowired
    private RestTemplate restTemplate;

    @Value("${audition.api.url}")
    private String auditionAPIUrl;

    public List<AuditionPost> getPosts() {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts
        try {
            return restTemplate.getForObject(auditionAPIUrl + "/posts", List.class);
        } catch (HttpClientErrorException e) { // 4XX exceptions
            throw new SystemException(
                "Client error occurred (status " + e.getStatusCode() + "): " + e.getMessage(),
                e.getStatusCode().value(),
                e
            );
        } catch (HttpServerErrorException e) { //5XX exception
            throw new SystemException(
                "Server error occurred (status " + e.getStatusCode() + "): " + e.getMessage(),
                e.getStatusCode().value(),
                e
            );
        } catch (RestClientException e) {
            throw new SystemException(
                "An error occurred: " + e.getMessage(),
                500,  // Defaulting to a general server error status code
                e
            );
        }
    }

    public AuditionPost getPostById(final String id) {
        // TODO get post by post ID call from https://jsonplaceholder.typicode.com/posts/
        try {

            // Fetch the post by ID
            AuditionPost post = restTemplate.getForObject(
                auditionAPIUrl + "/posts/" + id,
                AuditionPost.class);
            // Check if the post is null, which indicates it was not found
            if (post == null) {
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found", 404);
            }
            return post;
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found",
                    404);
            } else {
                // TODO Find a better way to handle the exception so that the original error message is not lost. Feel free to change this function.
                String errorMessage = e.getResponseBodyAsString(); // Extract the original error message
                throw new SystemException("Error retrieving post: " + errorMessage, e.getStatusCode().toString(),
                    e.getStatusCode().value());
            }
        }
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.

    public PostComment getPostWithComments(int postId) {
        // Fetch the post
        AuditionPost post = getPostById(String.valueOf(postId));

        // Fetch the comments for the post
        List<Comment> comments = restTemplate.getForObject(
            auditionAPIUrl + postId + "/comments",
            List.class);

        // Create a PostComment object to combine post and comments
        PostComment postComment = new PostComment();
        postComment.setPost(post);
        postComment.setComments(comments);

        return postComment;
    }

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.


    private SystemException handleHttpClientError(HttpClientErrorException e, String id) {
        HttpStatusCode statusCode = e.getStatusCode();
        String errorMessage = "An error occurred";
        String resource = "Resource";
        int errorCode = 0;

        if (statusCode.equals(HttpStatus.NOT_FOUND)) {
            errorMessage = "Cannot find a Post";
            resource = "Post";
            errorCode = HttpStatus.NOT_FOUND.value();
        } else if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            errorMessage = "Internal server error occurred";
            resource = "Resource";
            errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        } else if (statusCode.equals(HttpStatus.SERVICE_UNAVAILABLE)) {
            errorMessage = "Service temporarily unavailable";
            resource = "Service";
            errorCode = HttpStatus.SERVICE_UNAVAILABLE.value();
        }

        if (id != null) {
            errorMessage += " with id " + id;
        }

        return new SystemException(errorMessage, resource + " Error", errorCode);
    }
}



