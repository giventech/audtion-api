package com.audition.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.audition.common.util.AuditionUtils;
import com.audition.model.AuditionPost;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AuditionUtilTest {

    @Test
    void shouldReturnEmptyListWhenInputListIsEmpty() {
        final List<AuditionPost> posts = new ArrayList<>();
        final String filter = "test";
        final List<AuditionPost> result = AuditionUtils.getPostByIdAndTitleFilter(posts, filter);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertTrue(result.isEmpty(), "Result should be an empty list");
    }

    @Test
    void shouldFilterPostsByCaseInsensitivePartialMatchesInTitle() {
        final AuditionPost post1 = new AuditionPost(1, 23, "Java Developer", "Looking for a Java Developer");
        final AuditionPost post2 = new AuditionPost(2, 24, "Python Developer", "Looking for a Python Developer");
        final AuditionPost post3 = new AuditionPost(3, 25, "Senior Java Developer",
            "Looking for a Senior Java Developer");

        final List<AuditionPost> posts = Arrays.asList(post1, post2, post3);
        final List<AuditionPost> filteredPosts = AuditionUtils.getPostByIdAndTitleFilter(posts, "java");

        assertEquals(2, filteredPosts.size(), "Filtered posts size should be 2");
        assertEquals(post1, filteredPosts.get(0), "First filtered post should be post1");
        assertEquals(post3, filteredPosts.get(1), "Second filtered post should be post3");
    }
}
