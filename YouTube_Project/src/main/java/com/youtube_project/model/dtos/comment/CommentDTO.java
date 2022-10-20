package com.youtube_project.model.dtos.comment;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private long id;
    private String text;
    private UserResponseDTO owner;
    private int likes;
    private int dislikes;
    private LocalDateTime dateOfCreation;

}
