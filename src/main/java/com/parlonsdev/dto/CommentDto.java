package com.parlonsdev.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.parlonsdev.model.Post;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CommentDto {

    @NotBlank
    private String text;

    @NotBlank
    private Post post;

    public CommentDto() { }

    public CommentDto(@NotBlank String text, @NotBlank Post post) {
        this.text = text;
        this.post = post;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
