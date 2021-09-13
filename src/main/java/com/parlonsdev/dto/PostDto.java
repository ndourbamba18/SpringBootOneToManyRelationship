package com.parlonsdev.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PostDto {

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank
    private String content;

    public PostDto() { }

    public PostDto(@NotBlank @Size(min = 3, max = 100) String title, @NotBlank String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
