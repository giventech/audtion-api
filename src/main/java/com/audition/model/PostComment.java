package com.audition.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostComment {

    private AuditionPost post;
    private List<Comment> comments;

}
