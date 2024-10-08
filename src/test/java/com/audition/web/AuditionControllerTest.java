package com.audition.web;// AuditionControllerTest.java

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
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
    private MockMvc mockMvc;  // For simulating HTTP requests

    @MockBean
    private AuditionService auditionService;


    @MockBean
    private AuditionLogger auditionLogger;  // Mock the AuditionLogger

    @MockBean
    private Logger logger;  // Mock SLF4J Logger (if required, but mostly handled by AuditionLogger)

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
}
