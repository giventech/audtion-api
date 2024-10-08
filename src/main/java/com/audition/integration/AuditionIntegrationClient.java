package com.audition.integration;

import static com.audition.common.util.AuditionConstants.RETURNING_POST_WITH_COMMENTS_FOR_ID;

import com.audition.common.exception.SystemException;
import com.audition.common.util.AuditionConstants;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import com.audition.model.Comment;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class AuditionIntegrationClient {

    public static final String COMMENTS_POST_ID = "/comments?postId=";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${audition.api.url}")
    private String auditionAPIUrl;

    public List<AuditionPost> getPosts() {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts
        try {
            log.info(AuditionConstants.FETCHING_ALL_POSTS);
            return restTemplate.exchange(
                auditionAPIUrl + "/posts",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AuditionPost>>() {
                }
            ).getBody();
        } catch (RestClientException e) { // 4XX exceptions
            log.error(AuditionConstants.ERROR_RETRIEVING_ALL_POST);
            throw curatedServerException(e);
        }

    }


    public AuditionPost getPostById(final String id) {
        // TODO get post by post ID call from https://jsonplaceholder.typicode.com/posts/
        try {

            log.info("Fetching post with ID: {}", id);
            // Fetch the post by ID
            AuditionPost post = restTemplate.getForObject(
                auditionAPIUrl + "/posts/" + id,
                AuditionPost.class);
            // Return an empty post
            return Objects.requireNonNullElseGet(post, AuditionPost::new);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error(AuditionConstants.POST_ID, id, AuditionConstants.NO_RECORD_FOUND);
                throw new SystemException(AuditionConstants.NO_RECORD_FOUND + id, "Resource Not Found",
                    404);
            } else {
                // TODO Find a better way to handle the exception so that the original error message is not lost. Feel free to change this function.
                // Error message is collected prior to raising the exception
                log.error(AuditionConstants.POST_ID, e.getMessage(), e);
                String errorMessage = e.getResponseBodyAsString(); // Extract the original error message
                throw new SystemException(AuditionConstants.ERROR_RETRIEVING_POST + errorMessage,
                    e.getStatusCode().toString(),
                    e.getStatusCode().value());
            }
        }
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.

    public AuditionPostComments getPostWithComments(String postId) {
        try {

            log.info(AuditionConstants.FETCHING_POST_AND_COMMENTS_FOR_POST_ID, postId);
            // Fetch the post
            AuditionPost post = getPostById(String.valueOf(postId));
            log.debug(AuditionConstants.POST_FETCHED_SUCCESSFULLY, post);

            // Fetch the comments for the post
            List comments = restTemplate.getForObject(
                auditionAPIUrl + "/posts/" + postId + "/comments",
                List.class);
            log.debug(AuditionConstants.FETCHED_SUCCESSFULLY_FOR_POST_ID, postId);
            // Create a PostComment object to combine post and comments
            AuditionPostComments postComment = new AuditionPostComments();
            postComment.setPost(post);
            postComment.setComments(comments);
            log.info(RETURNING_POST_WITH_COMMENTS_FOR_ID, postId);

            return postComment;
        } catch (RestClientException e) { // 4XX exceptions
            log.error(AuditionConstants.ERROR_FETCHING_POST_OR_COMMENTS_FOR_POST_ID, postId, e);
            throw curatedServerException(e);
        }
    }

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.

    public List<Comment> getCommentsByPostId(int postId) {
        // Fetch the comments for the post
        try {
            log.info(AuditionConstants.FETCHING_COMMENTS_FOR_POST_ID, postId);
            return restTemplate.getForObject(
                auditionAPIUrl + COMMENTS_POST_ID + postId,
                List.class);
        } catch (RestClientException e) {
            log.error(AuditionConstants.ERROR_FETCHING_COMMENTS_FOR_POST_ID, postId, e);
            throw curatedServerException(e);
        }
    }


    public static SystemException curatedServerException(RestClientException e) {
        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException clientError = (HttpClientErrorException) e;
            return new SystemException(
                "Client error occurred (status " + clientError.getStatusCode() + "): " + clientError.getMessage(),
                clientError.getStatusCode().value(),
                clientError
            );
        } else if (e instanceof HttpServerErrorException serverError) {
            return new SystemException(
                "Server error occurred (status " + serverError.getStatusCode() + "): " + serverError.getMessage(),
                serverError.getStatusCode().value(),
                serverError
            );
        } else {
            return new SystemException(
                "An error occurred: " + e.getMessage(),
                500,  // Defaulting to a general server error status code
                e
            );
        }
    }

}



