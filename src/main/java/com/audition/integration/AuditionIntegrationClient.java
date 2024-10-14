package com.audition.integration;

import static com.audition.common.util.AuditionConstants.RETURNING_POST_WITH_COMMENTS_FOR_ID;

import com.audition.common.enumeration.BusinessErrorCode;
import com.audition.common.exception.BusinessException;
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
        try {
            log.info(AuditionConstants.FETCHING_ALL_POSTS);
            return restTemplate.exchange(
                auditionAPIUrl + "/posts",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AuditionPost>>() {
                }
            ).getBody();

        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error(AuditionConstants.POST_ID, AuditionConstants.NO_RECORD_FOUND);
                // No retry 404 which retry is unlikely to succeed
                throw new BusinessException(AuditionConstants.NO_RECORD_FOUND,
                    BusinessErrorCode.RESOURCE_NOT_FOUND.getCode(), e);
            } else {
                log.error(AuditionConstants.POST_ID, e.getMessage(), e);
                throw curatedServerException(e, e.getResponseBodyAsString(),
                    null); // Convert to SystemException
            }
        } catch (RestClientException e) {
            log.error(AuditionConstants.ERROR_RETRIEVING_ALL_POST);
            throw curatedServerException(e, AuditionConstants.ERROR_RETRIEVING_ALL_POST, null);
        }
    }

    public AuditionPost getPostById(final String id) {
        try {
            log.info("Fetching post with ID: {}", id);
            final AuditionPost post = restTemplate.getForObject(
                auditionAPIUrl + "/posts/" + id,
                AuditionPost.class);
            return Objects.requireNonNullElseGet(post, AuditionPost::new);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error(AuditionConstants.POST_ID, id, AuditionConstants.NO_RECORD_FOUND);
                // No retry 404 which retry is unlikely to succeed
                throw new BusinessException(AuditionConstants.NO_RECORD_FOUND,
                    BusinessErrorCode.RESOURCE_NOT_FOUND.getCode(), e);
            } else {
                log.error(AuditionConstants.POST_ID, e.getMessage(), e);
                throw curatedServerException(e, e.getResponseBodyAsString(),
                    Integer.valueOf(id)); // Convert to SystemException
            }
        } catch (RestClientException e) {
            log.error(AuditionConstants.ERROR_RETRIEVING_POST, e);
            throw curatedServerException(e, AuditionConstants.ERROR_RETRIEVING_POST, Integer.valueOf(id));
        }
    }

    public AuditionPostComments getPostWithComments(final String postId) {
        try {
            log.info(AuditionConstants.FETCHING_POST_AND_COMMENTS_FOR_POST_ID, postId);
            final AuditionPost post = getPostById(postId);
            log.debug(AuditionConstants.POST_FETCHED_SUCCESSFULLY, post);

            final List<Comment> comments = restTemplate.getForObject(
                auditionAPIUrl + "/posts/" + postId + "/comments",
                List.class);
            log.debug(AuditionConstants.FETCHED_SUCCESSFULLY_FOR_POST_ID, postId);
            final AuditionPostComments postComment = new AuditionPostComments();
            postComment.setPost(post);
            postComment.setComments(comments);
            log.info(RETURNING_POST_WITH_COMMENTS_FOR_ID, postId);
            return postComment;
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error(AuditionConstants.POST_ID, AuditionConstants.NO_RECORD_FOUND);
                // No retry 404 which retry is unlikely to succeed
                throw new BusinessException(AuditionConstants.NO_RECORD_FOUND,
                    BusinessErrorCode.RESOURCE_NOT_FOUND.getCode(), e);
            } else {
                log.error(AuditionConstants.POST_ID, e.getMessage(), e);
                throw curatedServerException(e, e.getResponseBodyAsString(),
                    null); // Convert to SystemException
            }

        } catch (RestClientException e) {
            log.error(AuditionConstants.ERROR_FETCHING_POST_OR_COMMENTS_FOR_POST_ID, postId, e);
            throw curatedServerException(e, AuditionConstants.ERROR_FETCHING_POST_OR_COMMENTS_FOR_POST_ID,
                Integer.valueOf(postId));
        }
    }


    public List<Comment> getCommentsByPostId(final int postId) {
        try {
            log.info(AuditionConstants.FETCHING_COMMENTS_FOR_POST_ID, postId);
            return restTemplate.getForObject(
                auditionAPIUrl + COMMENTS_POST_ID + postId,
                List.class);

        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error(AuditionConstants.POST_ID, AuditionConstants.NO_RECORD_FOUND);
                // No retry 404 which retry is unlikely to succeed
                throw new BusinessException(AuditionConstants.NO_RECORD_FOUND,
                    BusinessErrorCode.RESOURCE_NOT_FOUND.getCode(), e);
            } else {
                log.error(AuditionConstants.POST_ID, e.getMessage(), e);
                throw curatedServerException(e, e.getResponseBodyAsString(),
                    null); // Convert to SystemException
            }

        } catch (RestClientException e) {
            log.error(AuditionConstants.ERROR_FETCHING_COMMENTS_FOR_POST_ID, postId, e);
            throw curatedServerException(e, AuditionConstants.ERROR_FETCHING_COMMENTS_FOR_POST_ID, postId);
        }
    }


    public static SystemException curatedServerException(final RestClientException e, final String message,
        final Integer id) {
        final String originalMessage = " Original message:" + message + " Post ID  " + id;
        if (e instanceof HttpClientErrorException clientError) {
            return new SystemException(
                AuditionConstants.CLIENT_ERROR_OCCURRED + " (status " + clientError.getStatusCode() + "): "
                    + clientError.getMessage()
                    + originalMessage,
                clientError.getStatusCode().value(), e);
        } else if (e instanceof HttpServerErrorException serverError) {
            return new SystemException(
                AuditionConstants.SERVER_ERROR_OCCURRED + " (status " + serverError.getStatusCode() + "): "
                    + serverError.getMessage()
                    + originalMessage,
                serverError.getStatusCode().value(), e);
        } else {
            return new SystemException(
                "An error occurred: " + e.getMessage() + originalMessage, 500, e); // Preserve stack trace
        }
    }
}
