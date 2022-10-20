package com.youtube_project.model.dtos.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private long id;
    private String text;
    private LocalDateTime dateOfCreation;

}
