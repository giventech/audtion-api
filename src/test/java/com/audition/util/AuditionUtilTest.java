package com.audition.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.audition.common.util.AuditionUtils;
import com.audition.model.AuditionPost;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuditionUtilTest {

    @Test
    void shouldReturnEmptyListWhenInputListIsEmpty() {
        List<AuditionPost> posts = new ArrayList<>();
        String filter = "test";
        List<AuditionPost> result = AuditionUtils.getPostByIdAndTitleFilter(posts, filter);
        Assertions.assertNotNull((result));
        Assertions.assertTrue((result).isEmpty());
    }


    @Test
    void shouldFilterPostsByCaseInsensitivePartialMatchesInTitle() {
        AuditionPost post1 = new AuditionPost(1, 23, "Java Developer", "Looking for a Java Developer");
        AuditionPost post2 = new AuditionPost(2, 24, "Python Developer", "Looking for a Python Developer");
        AuditionPost post3 = new AuditionPost(3, 25, "Senior Java Developer", "Looking for a Senior Java Developer");

        List<AuditionPost> posts = Arrays.asList(post1, post2, post3);
        List<AuditionPost> filteredPosts = AuditionUtils.getPostByIdAndTitleFilter(posts, "java");

        assertEquals(2, filteredPosts.size());
        assertEquals(post1, filteredPosts.get(0));
        assertEquals(post3, filteredPosts.get(1));
    }

}
