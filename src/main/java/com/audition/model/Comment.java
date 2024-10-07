package com.audition.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Comment {

    private int postId;
    private int id;
    private String name;
    private String email;
    private String body;
}
