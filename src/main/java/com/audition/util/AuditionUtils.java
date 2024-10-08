package com.audition.util;

import com.audition.model.AuditionPost;
import java.util.List;
import java.util.stream.Collectors;

public class AuditionUtils {

    /**
     * This function filters a list of {@link AuditionPost} objects based on the provided filter string. The filter
     * checks if the title or body of each post contains the given filter string. The function returns a new list
     * containing only the posts that satisfy the filter condition.
     *
     * @param posts  The list of {@link AuditionPost} objects to be filtered.
     * @param filter The filter string to be used for filtering the posts.
     * @return A new list of {@link AuditionPost} objects that satisfy the filter condition.
     */
    public static List<AuditionPost> getPostByIdAndTitleFilter(List<AuditionPost> posts, String filter) {
        String caseInsensitiveFilter = filter.toLowerCase();
        return posts.stream()
            .filter(
                post -> post.getTitle().toLowerCase().contains(caseInsensitiveFilter) || post.getBody().toLowerCase()
                    .contains(caseInsensitiveFilter))
            .collect(Collectors.toList());
    }
}