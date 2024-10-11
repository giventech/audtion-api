package com.audition.web;

import com.audition.common.util.AuditionUtils;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import com.audition.service.AuditionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AuditionController {

    @Autowired
    AuditionService auditionService;

    // TODO Add a query param that allows data filtering. The intent of the filter is at developers discretion.
    @Validated
    @Operation(summary = " Retrieves all posts or the  posts that match the filter")
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(
        @RequestParam(value = "filter", required = false) final String filter) {

        // Get all posts from the service
        List<AuditionPost> posts = auditionService.getPosts();

        // If a filter query parameter is provided, apply filtering logic

        if (filter != null && !filter.isEmpty()) {
            posts = AuditionUtils.getPostByIdAndTitleFilter(posts, filter);
        }

        // If no filter is provided, return all posts
        return posts;
    }


    @Validated
    @Operation(summary = "Get all posts for a given post id")
    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostsById(
        @Valid
        @NotEmpty(message = "Post id must not be empty")
        @Positive(message = "Post id must be positive integer")
        @Parameter(description = "ID of the post to retrieve", required = true)
        @PathVariable("id") final String postId) {

        return auditionService.getPostById(postId);

    }

    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/

    @Validated
    @Operation(summary = "Get a post and all its given comments")
    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPostComments getCommentsForPost(
        @Valid
        @NotEmpty(message = "Post id must not be empty")
        @Positive(message = "Post id must be positive integer")
        @PathVariable("id") final String postId) {

        return auditionService.getPostAndComments(postId);


    }


}


