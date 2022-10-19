package com.youtube_project.models.comment;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private long id;
    private String text;
    private LocalDateTime dateOfCreation;

}
