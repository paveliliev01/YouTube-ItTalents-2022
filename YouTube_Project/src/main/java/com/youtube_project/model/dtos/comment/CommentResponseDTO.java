package com.youtube_project.model.dtos.comment;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class CommentResponseDTO {
    private long id;
    private String text;
    private int likes;
    private int dislikes;
    private UserResponseDTO owner;
    private List<CommentDTO> subComments;
}
