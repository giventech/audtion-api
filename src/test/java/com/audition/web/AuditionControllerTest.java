package com.audition.web;// AuditionControllerTest.java

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuditionController.class)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditionService auditionService;


    @MockBean
    private AuditionLogger auditionLogger;

    @MockBean
    private Logger logger;

    @Test
    void testGetPostsWithoutFilter() throws Exception {
        // Mock service response
        AuditionPost post1 = new AuditionPost(1, 22, "Title 1", "Body 1");
        AuditionPost post2 = new AuditionPost(2, 23, "Title 2", "Body 2");
        List<AuditionPost> mockPosts = Arrays.asList(post1, post2);

        // Mock the behavior of the auditionService
        Mockito.when(auditionService.getPosts()).thenReturn(mockPosts);

        // Perform a GET request and verify the response
        mockMvc.perform(get("/posts")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())  // Check that the status is 200 OK
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Check that the content type is JSON
            .andExpect(jsonPath("$.length()").value(2))  // Check that the returned list has 2 items
            .andExpect(jsonPath("$[0].id").value(22))  // Check the first post's ID
            .andExpect(jsonPath("$[0].title").value("Title 1"))  // Check the first post's title
            .andExpect(jsonPath("$[1].id").value(23))  // Check the second post's ID
            .andExpect(jsonPath("$[1].title").value("Title 2"));  // Check the second post's title
    }

    @Test
    void testGetPostsWithFilter() throws Exception {
        // Mock service response
        AuditionPost post1 = new AuditionPost(1, 22, "Filtered Title", "Body 1");
        List<AuditionPost> filteredPosts = Arrays.asList(post1);

        // Mock the behavior of the auditionService
        Mockito.when(auditionService.getPosts()).thenReturn(filteredPosts);

        // Perform a GET request with filter and verify the response
        mockMvc.perform(get("/posts")
                .param("filter", "Filtered Title")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())  // Check that the status is 200 OK
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Check that the content type is JSON
            .andExpect(jsonPath("$.length()").value(1))  // Check that the returned list has 1 item
            .andExpect(jsonPath("$[0].title").value("Filtered Title"));  // Check the filtered post's title
    }

    @Test
    void testGetPostWithComments() throws Exception {
        // Mock service response
        AuditionPost post1 = new AuditionPost(1, 22, "Post with Comments", "Body 1");
        AuditionPostComments postComments = new AuditionPostComments(post1,
            Arrays.asList(
                new Comment(1, 1, "just a name", "test@email.com", "Comment 1"),
                new Comment(1, 2, "just a another name", "test2@email.com", "Comment 2"),
                new Comment(1, 3, "just a another name", "test2@email.com", "Comment 3")
            )
        );
        // Mock the behavior of the auditionService
        Mockito.when(auditionService.getPostById(Mockito.eq("1"))).thenReturn(post1);
        Mockito.when(auditionService.getPostAndComments(Mockito.eq("1"))).thenReturn(postComments);

        // Perform a GET request for a specific post and verify the response
        mockMvc.perform(get("/posts/1/comments")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())  // Check that the status is 200 OK
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Check that the content type is JSON
            .andExpect(jsonPath("$.post.title").value("Post with Comments"))  // Check the post's title
            .andExpect(
                jsonPath("$.comments.length()").value(3))  // Check that the returned list of comments has 2 items
            .andExpect(jsonPath("$.comments[0].body").value("Comment 1"))  // Check the first comment's body
            .andExpect(jsonPath("$.comments[1].body").value("Comment 2"))  // Check the second comment's body
            .andExpect(jsonPath("$.comments[2].body").value("Comment 3"));  // Check the second comment's body
    }

}
